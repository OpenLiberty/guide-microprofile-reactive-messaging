// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.order;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.Type;
import io.openliberty.guides.order.OrderResource;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class OrderEndpointIT {

    private static final String CONSUMER_OFFSET_RESET = "earliest";
    private static final long POLL_TIMEOUT = 30 * 1000;

    @RESTClient
    public static OrderResource orderResource;

    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    private static ArrayList<Order> orderList = new ArrayList<Order>();

    private static Jsonb jsonb;

    @BeforeAll
    public static void setup() {
        // init test data
        orderList.add(new Order().setItem("Pizza").setType(Type.FOOD).setTableId("0001"));
        orderList.add(new Order().setItem("Burger").setType(Type.FOOD).setTableId("0001"));
        orderList.add(new Order().setItem("Coke").setType(Type.BEVERAGE).setTableId("0002"));

        // init kafka producer & consumer
        String KAFKA_SERVER = AppContainerConfig.kafka.getBootstrapServers();

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(properties);

        properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "update-status");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_RESET);
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("foodTopic", "beverageTopic"));

        jsonb = JsonbBuilder.create();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testGetStatus() {
        Response response = orderResource.getStatus();

        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testInitFoodOrder() {
        for (int i = 0; i < orderList.size(); i++) {
            Response res = orderResource.createOrder(orderList.get(i));

            Assertions.assertEquals(200, res.getStatus(),
                    "Response should be 200");

            Order order = orderList.get(i);
            Order orderRes = res.readEntity(Order.class);

            Assertions.assertEquals(order.getTableId(), orderRes.getTableId(),
                    "Table Id from response does not match");
            Assertions.assertEquals(order.getItem(), orderRes.getItem(),
                    "Item from response does not match");
            Assertions.assertEquals(order.getType(), orderRes.getType(),
                    "Type from response does not match");

            Assertions.assertTrue(orderRes.getOrderId() != null,
                    "Order Id from response is null");
            Assertions.assertEquals(orderRes.getStatus(), Status.NEW,
                    "Status from response should be NEW");

            // replace input order with response order (includes orderId and status)
            orderList.set(i, orderRes);
        }

        // verify the order is sent correctly to kafka
        verify();
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testGetOrderList() {
        Response response = orderResource.getOrdersList(null);
        ArrayList<Order> orders = response.readEntity(new GenericType<ArrayList<Order>>() {});

        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");

        for (Order order : orderList) {
            Assertions.assertTrue(orders.contains(order),
                    "Order " + order.getOrderId() + " not found in response");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void testGetOrderListByTableId() {
        final String tableId = "0001";

        Response response = orderResource.getOrdersList(tableId);
        ArrayList<Order> orders = response.readEntity(new GenericType<ArrayList<Order>>() {});

        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");

        for (Order order : orderList) {
            if (order.getTableId().equals(tableId))
                Assertions.assertTrue(orders.contains(order),
                        "Order " + order.getOrderId() + " not in found response");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    public void testGetOrder() {
        Order order = orderList.get(0);

        Response response = orderResource.getOrder(order.getOrderId());

        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");

        Assertions.assertEquals(order, response.readEntity(Order.class),
                "Order " + order.getOrderId() + " from response does not match");
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void testUpdateOrder() {
        Order order = orderList.get(0);
        order.setStatus(Status.IN_PROGRESS);

        producer.send(new ProducerRecord<>("statusTopic", jsonb.toJson(order)));

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        Response response = orderResource.getOrder(order.getOrderId());

        Assertions.assertEquals(order, response.readEntity(Order.class),
                "Order " + order.getOrderId() + " from response does not match");
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    public void testOrderDNE() {
        Response res = orderResource.getOrder("openliberty");

        Assertions.assertEquals(404, res.getStatus(),
                "Response should be 404");
    }

    private void verify() {
        int expectedRecords = orderList.size();
        int recordsMatched = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        Order order;

        while (recordsMatched < expectedRecords && elapsedTime < POLL_TIMEOUT) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
            for (ConsumerRecord<String, String> record : records) {
                order = jsonb.fromJson(record.value(), Order.class);
                if (orderList.contains(order))
                    recordsMatched++;
            }
            consumer.commitAsync();
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        Assertions.assertTrue(recordsMatched == expectedRecords,
                "Kafka did not receive orders correctly");
    }
}
