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
package it.io.openliberty.guides.servingWindow;

import java.time.Duration;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
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
import io.openliberty.guides.servingWindow.ServingWindowResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class ServingWindowEndpointIT {

    private static final long POLL_TIMEOUT = 30 * 1000;
    
    @RESTClient
    public static ServingWindowResource servingWindowResource;
    
    @KafkaProducerConfig(valueSerializer = JsonbSerializer.class)
    public static KafkaProducer<String, Order> producer;

    @KafkaConsumerConfig(valueDeserializer = OrderDeserializer.class, 
        groupId = "update-status", topics = "statusTopic", 
        properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, Order> consumer;

    private static Order order;
    
    @Test
    @org.junit.jupiter.api.Order(1)
    public void testAddReadyOrder() throws InterruptedException {
        order = new Order("0001", "1", Type.FOOD, "burger", Status.READY);
        producer.send(new ProducerRecord<String, Order>("statusTopic", order));
        Thread.sleep(5000);
        Assertions.assertEquals(1, getReadyListSize(), "No ready order was added.");
        verify(Status.READY);
    }
    
    @Test
    @org.junit.jupiter.api.Order(2)
    public void testMarkOrderComplete() throws InterruptedException {
        servingWindowResource.markOrderComplete("0001");
        Thread.sleep(5000);
        Assertions.assertEquals(0, getReadyListSize(), "The order was not removed.");
        verify(Status.COMPLETED);
    }
    
    @Test
    @org.junit.jupiter.api.Order(3)
    public void testMarkOrderCompleteNotFound() throws InterruptedException {
        Response response = servingWindowResource.markOrderComplete("unknown");
        Assertions.assertEquals(404, response.getStatus());
    }
    
    private int getReadyListSize() {
        Response response = servingWindowResource.listContents();
        return response.readEntity(ArrayList.class).size();
    }
    
    private void verify(Status expectedStatus) {
        int recordsProcessed = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (recordsProcessed == 0 && elapsedTime < POLL_TIMEOUT) {
            ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(3000));
            System.out.println("Polled " + records.count() + " records from Kafka:");
            for (ConsumerRecord<String, Order> record : records) {
                order = record.value();
                System.out.println(order);
                Assertions.assertEquals(expectedStatus,order.getStatus());
                recordsProcessed++;
            }
            consumer.commitAsync();
            if (recordsProcessed > 0)
                break;
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        Assertions.assertTrue(recordsProcessed > 0, "No records processed");
    }

}
