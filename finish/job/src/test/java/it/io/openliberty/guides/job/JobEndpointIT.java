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
package it.io.openliberty.guides.job;

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

import io.openliberty.guides.job.JobResource;
import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.Job.JsonbSerializer;
import io.openliberty.guides.models.Job.JobDeserializer;
import io.openliberty.guides.models.Status;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class JobEndpointIT {

    private static final long POLL_TIMEOUT = 30 * 1000;

    @RESTClient
    public static JobResource jobResource;

    @KafkaProducerConfig(valueSerializer = JsonbSerializer.class)
    public static KafkaProducer<String, Job> producer;

    @KafkaConsumerConfig(valueDeserializer = JobDeserializer.class, 
        groupId = "job-consumer", topics = "jobTopic", 
        properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, Job> jobConsumer;

    @KafkaConsumerConfig(valueDeserializer = JobDeserializer.class, 
            groupId = "update-status", topics = "statusTopic", 
            properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    public static KafkaConsumer<String, Job> statusConsumer;
        
    private static ArrayList<Job> jobList = new ArrayList<Job>();

    @BeforeAll
    public static void setup() {
        // init test data
    	jobList.add(new Job(null, "127.0.0.1", "task desc 1", null));
    	jobList.add(new Job(null, "127.0.0.2", "task desc 2", null));
    	jobList.add(new Job(null, "127.0.0.3", "task desc 3", null));
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testGetStatus() {
        Response response = jobResource.getStatus();
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testInitJob() {
        for (int i = 0; i < jobList.size(); i++) {
            Response res = jobResource.createJob(jobList.get(i));

            Assertions.assertEquals(200, res.getStatus(),
                    "Response should be 200");

            Job job = jobList.get(i);
            Job jobRes = res.readEntity(Job.class);

            Assertions.assertEquals(job.hostId, jobRes.hostId,
                    "Host Id from response does not match");
            Assertions.assertEquals(job.taskDesc, jobRes.taskDesc,
                    "Task description from response does not match");

            Assertions.assertTrue(jobRes.jobId != null,
                    "Job Id from response is null");
            Assertions.assertEquals(jobRes.status, Status.NEW,
                    "Status from response should be NEW");

            // replace input job with response job (includes jobId and status)
            jobList.set(i, jobRes);
        }

        // verify the job is sent correctly to kafka
        verify(jobConsumer, 3);
        verify(statusConsumer, 3);
    }

    private void verify(KafkaConsumer<String, Job> consumer, int expectedRecords) {
        int recordsMatched = 0;
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        Job job;

        while (recordsMatched < expectedRecords && elapsedTime < POLL_TIMEOUT) {
            ConsumerRecords<String, Job> records = consumer.poll(Duration.ofMillis(3000));
            System.out.println("Polled " + records.count() + " records from Kafka:");
            for (ConsumerRecord<String, Job> record : records) {
                job = record.value();
                if (jobList.contains(job))
                    recordsMatched++;
            }
            consumer.commitAsync();
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        Assertions.assertEquals(expectedRecords, recordsMatched,
                "Kafka did not receive jobs correctly");
    }
}
