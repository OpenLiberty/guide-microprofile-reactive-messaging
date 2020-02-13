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
package it.io.openliberty.guides.restaurantbff;

import io.openliberty.guides.models.OrderRequest;
import io.openliberty.guides.restaurantbff.client.OrderClient;
import io.openliberty.guides.restaurantbff.client.ServingWindowClient;
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

    public static ArrayList<String> foodList = new ArrayList<>();
    public static ArrayList<String> beverageList = new ArrayList<>();
    public static OrderRequest orderRequest = new OrderRequest();

    @Container
    public static MockServerContainer mockServer = new MockServerContainer()
            .withNetworkAliases("mock-server")
            .withNetwork(network);

    @Container
    public static ApplicationContainer mockApp = new ApplicationContainer()
            .withAppContextRoot("/")
            .withReadinessPath("/api/orders")
            .withReadinessPath("/api/servingWindow")
            .withNetwork(network)
            .withMpRestClient(OrderClient.class, "http://mock-server:" + MockServerContainer.PORT)
            .withEnv("ORDER_SERVICE_HOSTNAME", "mock-server")
            .withEnv("ORDER_SERVICE_PORT", String.valueOf(MockServerContainer.PORT))
            .withMpRestClient(ServingWindowClient.class, "http://mock-server:" + MockServerContainer.PORT)
            .withEnv("SERVINGWINDOW_SERVICE_HOSTNAME", "mock-server")
            .withEnv("SERVINGWINDOW_SERVICE_PORT", String.valueOf(MockServerContainer.PORT));

    @Override
    public void startContainers() {
        mockServer.start();
        mockClient = new MockServerClient(
                mockServer.getContainerIpAddress(),
                mockServer.getServerPort());

        //For getOrders() in Order Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/orders"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        //For getSingleOrder("0001") in Order Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/orders/0001"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        if(foodList.isEmpty()){
            foodList.add("Pho");
            beverageList.add("Iced Tea");
            orderRequest.setTableID("10");
            orderRequest.setFoodList(foodList);
            orderRequest.setBeverageList(beverageList);
        }

        //For createOrder() in Order Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/orders")
                        .withBody("{\"beverageList\":[\"Iced Tea\"],\"foodList\":[\"Pho\"],\"tableID\":\"10\"}"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        //For getReady2Serve() in ServingWindow Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/servingWindow"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        //For serveOrder() in ServingWindow Client
        mockClient
                .when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/servingWindow/complete/0001"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        mockApp.start();
    }

}
