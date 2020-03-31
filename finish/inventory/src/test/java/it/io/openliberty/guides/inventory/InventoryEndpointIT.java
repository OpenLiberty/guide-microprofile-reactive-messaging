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
package it.io.openliberty.guides.inventory;

import java.util.ArrayList;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaProducerConfig;

import io.openliberty.guides.inventory.InventoryResource;
import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.Job.JsonbSerializer;
import io.openliberty.guides.models.Status;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class InventoryEndpointIT {
	
    @RESTClient
    public static InventoryResource inventoryResource;

    @KafkaProducerConfig(valueSerializer = JsonbSerializer.class)
    public static KafkaProducer<String, Job> producer;

    private static ArrayList<Job> jobList = new ArrayList<Job>();

    @BeforeAll
    public static void setup() {
        // init test data
    	jobList.add(new Job("0001", "127.0.0.1", "task desc 1", Status.NEW));
    	jobList.add(new Job("0002", "127.0.0.2", "task desc 2", Status.NEW));
    	jobList.add(new Job("0003", "127.0.0.1", "task desc 3", Status.NEW));
    }
    
    @AfterAll
    public static void cleanup() {
    	inventoryResource.resetJob();
    }

    @Test
    @Order(1)
    public void testGetJobList() throws InterruptedException {
        for (int i = 0; i < jobList.size(); i++) {
        	producer.send(new ProducerRecord<String, Job>("statusTopic", jobList.get(i)));
        }
        Thread.sleep(10000);
        Response response = inventoryResource.getJobsList();
        ArrayList<Job> jobs = response.readEntity(new GenericType<ArrayList<Job>>() {});
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
        Assertions.assertEquals(jobList.size(), jobs.size());
        for (Job job : jobList) {
        	System.out.println(job.jobId + "," +  job.status);
            Assertions.assertTrue(jobs.contains(job),
                "Job " + job.jobId + " not found in response");
        }
    }

    @Test
    @Order(2)
    public void testGetJobListByHostId() {
        String hostId = jobList.get(0).hostId;
        Response response = inventoryResource.getJobsList(hostId);
        ArrayList<Job> jobs = response.readEntity(new GenericType<ArrayList<Job>>() {});
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
        Assertions.assertEquals(2, jobs.size());
        for (Job job : jobs) {
        	Assertions.assertEquals(hostId, job.hostId);
        }
    }

    @Test
    @Order(3)
    public void testGetJob() throws InterruptedException {
        Job job = jobList.get(1);
        Response response = inventoryResource.getJob(job.jobId);
        Assertions.assertEquals(200, response.getStatus(),
                "Response should be 200");
        Assertions.assertEquals(job, response.readEntity(Job.class),
                "Job " + job.jobId + " from response does not match");
    }

    @Test
    @Order(4)
    public void testUpdateJob() throws InterruptedException {
        Job job = jobList.get(0);
        job.status = Status.IN_PROGRESS;
        producer.send(new ProducerRecord<String, Job>("statusTopic", job));
        Thread.sleep(1000);
        Response response = inventoryResource.getJob(job.jobId);
        Assertions.assertEquals(job, response.readEntity(Job.class),
                "Job " + job.jobId + " from response does not match");
    }

    @Test
    @Order(5)
    public void testJobDNE() {
        Response res = inventoryResource.getJob("openliberty");
        Assertions.assertEquals(404, res.getStatus(),
                "Response should be 404");
    }
}
