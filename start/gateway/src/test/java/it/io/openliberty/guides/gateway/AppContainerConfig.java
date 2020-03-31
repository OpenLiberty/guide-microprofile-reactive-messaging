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
package it.io.openliberty.guides.gateway;

import io.openliberty.guides.models.JobRequest;
import io.openliberty.guides.gateway.client.JobClient;
import io.openliberty.guides.gateway.client.InventoryClient;

import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;

import java.util.ArrayList;

public class AppContainerConfig implements SharedContainerConfiguration {

    private static Network network = Network.newNetwork();
    public static MockServerClient mockClient;

    public static ArrayList<String> jobList = new ArrayList<>();
    public static JobRequest jobRequest = new JobRequest();

    @Container
    public static MockServerContainer mockServer = new MockServerContainer()
            .withNetworkAliases("mock-server")
            .withNetwork(network);

    @Container
    public static ApplicationContainer mockApp = new ApplicationContainer()
            .withAppContextRoot("/")
            .withReadinessPath("/api/jobs")
            .withReadinessPath("/api/inventory/jobs")
            .withNetwork(network)
            .withMpRestClient(JobClient.class, "http://mock-server:" + MockServerContainer.PORT)
            .withMpRestClient(InventoryClient.class, "http://mock-server:" + MockServerContainer.PORT);

    @Override
    public void startContainers() {
        mockServer.start();
        mockClient = new MockServerClient(
                mockServer.getContainerIpAddress(),
                mockServer.getServerPort());

        //For getJobs() in Inventory Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/inventory/jobs"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        //For getSingleJob("0001") in Inventory Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/inventory/job/0001"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        //For getJobsList("0001") in Inventory Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/inventory/host/0001"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));
        
        if(jobList.isEmpty()){
            jobList.add("task desc 1");
            jobRequest.setTaskList(jobList);
        }

        //For createJob() in Job Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/jobs")
                        .withBody("{\"taskList\":[\"task desc 1\"]}"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        mockApp.start();
    }

}
