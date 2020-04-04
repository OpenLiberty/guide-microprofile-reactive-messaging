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
package it.io.openliberty.guides.system;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaConsumerConfig;
import org.microshed.testing.kafka.KafkaProducerConfig;

import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.Job.JsonbSerializer;
import io.openliberty.guides.models.Job.JobDeserializer;
import io.openliberty.guides.models.Status;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class SystemServiceIT {

    private static final long POLL_TIMEOUT = 30 * 1000;

    @KafkaProducerConfig(valueSerializer = JsonbSerializer.class)
    public static KafkaProducer<String, Job> producer;

    @KafkaConsumerConfig(valueDeserializer = JobDeserializer.class, 
        groupId = "update-status", topics = "statusTopic", 
        properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, Job> consumer;

    private static Job job;
   
    @Test
    public void testInitFoodOrder() throws IOException, InterruptedException {
        job = new Job("0001", null, "tast desc 1", Status.NEW);
        producer.send(new ProducerRecord<String, Job>("jobTopic", job));
        verify(Status.IN_PROGRESS);
        Thread.sleep(10000);
        verify(Status.COMPLETED);
    }
        
    private void verify(Status expectedStatus) {
        int recordsProcessed = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (recordsProcessed == 0 && elapsedTime < POLL_TIMEOUT) {
            ConsumerRecords<String, Job> records = consumer.poll(Duration.ofMillis(3000));
            System.out.println("Polled " + records.count() + " records from Kafka:");
            for (ConsumerRecord<String, Job> record : records) {
                job = record.value();
                System.out.println(job);
                assertEquals("0001",job.jobId);
                assertNotNull(job.hostId);
                assertEquals(expectedStatus,job.status);
                recordsProcessed++;
            }
            consumer.commitAsync();
            if (recordsProcessed > 0)
                break;
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        assertTrue(recordsProcessed > 0, "No records processed");
    }
}
