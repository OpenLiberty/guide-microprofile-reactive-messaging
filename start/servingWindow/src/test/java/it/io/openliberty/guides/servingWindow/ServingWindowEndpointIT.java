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
import java.util.Arrays;
import java.util.Properties;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;

import io.openliberty.guides.models.Status;
import io.openliberty.guides.models.Type;
import io.openliberty.guides.servingWindow.ServingWindowResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class ServingWindowEndpointIT {
    private static final String CONSUMER_OFFSET_RESET = "earliest";
    private static final long POLL_TIMEOUT = 30 * 1000;
    
    @RESTClient
    public static ServingWindowResource servingWindowResource;
    
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    private static io.openliberty.guides.models.Order order;
    private static Jsonb jsonb = JsonbBuilder.create();
    
    @BeforeAll
    public static void setup() throws InterruptedException {

        String KAFKA_SERVER = AppContainerConfig.kafka.getBootstrapServers();
        
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(properties);

        properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "update-status");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_OFFSET_RESET);
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("statusTopic"));
    }

    @Test
    @Order(1)
    public void testAddReadyOrder() throws InterruptedException {
    	order = new io.openliberty.guides.models.Order("0001", "1", Type.FOOD, "burger", Status.READY);
    	String jOrder = JsonbBuilder.create().toJson(order);
        producer.send(new ProducerRecord<String, String>("statusTopic", jOrder));
        Thread.sleep(5000);
        Assertions.assertEquals(1, getReadyListSize(), "No ready order was added.");
        verify(Status.READY);
    }
    
    @Test
    @Order(2)
    public void testMarkOrderComplete() throws InterruptedException {
    	servingWindowResource.markOrderComplete("0001");
    	Thread.sleep(5000);
        Assertions.assertEquals(0, getReadyListSize(), "The order was not removed.");
        verify(Status.COMPLETED);
    }
    
    @Test
    @Order(3)
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
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
            System.out.println("Polled " + records.count() + " records from Kafka:");
            for (ConsumerRecord<String, String> record : records) {
            	System.out.println(record.value());
				order = jsonb.fromJson(record.value(), io.openliberty.guides.models.Order.class);
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
