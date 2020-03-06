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
package it.io.openliberty.guides.status;

import java.util.ArrayList;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaProducerConfig;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Order.JsonbSerializer;
import io.openliberty.guides.models.Status;
import io.openliberty.guides.models.Type;
import io.openliberty.guides.status.StatusResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class StatusEndpointIT {

    @RESTClient
    public static StatusResource statusResource;

    @KafkaProducerConfig(valueSerializer = JsonbSerializer.class)
    public static KafkaProducer<String, Order> producer;

    private static ArrayList<Order> orderList = new ArrayList<Order>();

    @BeforeAll
    public static void setup() {
        // init test data
        orderList.add(new Order().setOrderId("0001").setItem("Pizza").setType(Type.FOOD).setTableId("T1").setStatus(Status.NEW));
        orderList.add(new Order().setOrderId("0002").setItem("Burger").setType(Type.FOOD).setTableId("T1").setStatus(Status.NEW));
        orderList.add(new Order().setOrderId("0003").setItem("Coke").setType(Type.BEVERAGE).setTableId("T2").setStatus(Status.NEW));
    }
    
    @AfterAll
    public static void cleanup() {
    	statusResource.resetOrder();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testGetOrderList() throws InterruptedException {
        for (int i = 0; i < orderList.size(); i++) {
        	producer.send(new ProducerRecord<String, Order>("statusTopic", orderList.get(i)));
        }
        Thread.sleep(10000);
        Response response = statusResource.getOrdersList();
        ArrayList<Order> orders = response.readEntity(new GenericType<ArrayList<Order>>() {});
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
        Assertions.assertEquals(orderList.size(), orders.size());
        for (Order order : orderList) {
        	System.out.println(order.getOrderId() + "," +  order.getStatus());
            Assertions.assertTrue(orders.contains(order),
                "Order " + order.getOrderId() + " not found in response");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testGetOrderListByTableId() {
        String tableId = orderList.get(0).getTableId();
        Response response = statusResource.getOrdersList(tableId);
        ArrayList<Order> orders = response.readEntity(new GenericType<ArrayList<Order>>() {});
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
        Assertions.assertEquals(2, orders.size());
        for (Order order : orders) {
        	Assertions.assertEquals(tableId, order.getTableId());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testGetOrder() throws InterruptedException {
        Order order = orderList.get(1);
        Response response = statusResource.getOrder(order.getOrderId());
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
        Assertions.assertEquals(order, response.readEntity(Order.class),
                "Order " + order.getOrderId() + " from response does not match");
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void testUpdateOrder() throws InterruptedException {
        Order order = orderList.get(0);
        order.setStatus(Status.IN_PROGRESS);
        producer.send(new ProducerRecord<String, Order>("statusTopic", order));
        Thread.sleep(1000);
        Response response = statusResource.getOrder(order.getOrderId());
        Assertions.assertEquals(order, response.readEntity(Order.class),
                "Order " + order.getOrderId() + " from response does not match");
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    public void testOrderDNE() {
        Response res = statusResource.getOrder("openliberty");
        Assertions.assertEquals(404, res.getStatus(),
                "Response should be 404");
    }
}
