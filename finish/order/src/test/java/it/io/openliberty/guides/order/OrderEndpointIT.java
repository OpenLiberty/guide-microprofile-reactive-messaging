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

import javax.ws.rs.core.Response;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaConsumerConfig;
import org.microshed.testing.kafka.KafkaProducerConfig;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Order.JsonbSerializer;
import io.openliberty.guides.models.Order.OrderDeserializer;
import io.openliberty.guides.models.Status;
import io.openliberty.guides.models.Type;
import io.openliberty.guides.order.OrderResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class OrderEndpointIT {

    private static final long POLL_TIMEOUT = 30 * 1000;

    @RESTClient
    public static OrderResource orderResource;

    @KafkaProducerConfig(valueSerializer = JsonbSerializer.class)
    public static KafkaProducer<String, Order> producer;

    @KafkaConsumerConfig(valueDeserializer = OrderDeserializer.class, 
        groupId = "food-consumer", topics = "foodTopic", 
        properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, Order> foodConsumer;
    
    @KafkaConsumerConfig(valueDeserializer = OrderDeserializer.class, 
            groupId = "beverage-consumer", topics = "beverageTopic", 
            properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
        public static KafkaConsumer<String, Order> beverageConsumer;
    
    @KafkaConsumerConfig(valueDeserializer = OrderDeserializer.class, 
            groupId = "update-status", topics = "statusTopic", 
            properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, Order> statusConsumer;
        
    private static ArrayList<Order> orderList = new ArrayList<Order>();

    @BeforeAll
    public static void setup() {
        // init test data
        orderList.add(new Order().setItem("Pizza").setType(Type.FOOD).setTableId("0001"));
        orderList.add(new Order().setItem("Burger").setType(Type.FOOD).setTableId("0001"));
        orderList.add(new Order().setItem("Coke").setType(Type.BEVERAGE).setTableId("0002"));
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
        verify(foodConsumer, 2);
        verify(beverageConsumer, 1);
        verify(statusConsumer, 3);
    }

    private void verify(KafkaConsumer<String, Order> consumer, int expectedRecords) {
        int recordsMatched = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        Order order;

        while (recordsMatched < expectedRecords && elapsedTime < POLL_TIMEOUT) {
            ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(3000));
            System.out.println("Polled " + records.count() + " records from Kafka:");
            for (ConsumerRecord<String, Order> record : records) {
                order = record.value();
                if (orderList.contains(order))
                    recordsMatched++;
            }
            consumer.commitAsync();
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        Assertions.assertEquals(expectedRecords, recordsMatched,
                "Kafka did not receive orders correctly");
    }
}
