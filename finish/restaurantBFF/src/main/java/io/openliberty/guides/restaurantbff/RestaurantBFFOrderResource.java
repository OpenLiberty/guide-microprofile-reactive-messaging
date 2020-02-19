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
package io.openliberty.guides.restaurantbff;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.OrderRequest;
import io.openliberty.guides.models.Type;
import io.openliberty.guides.restaurantbff.client.OrderClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@ApplicationScoped
@Path("/orders")
public class RestaurantBFFOrderResource {

    @Inject
    private Validator validator;

    @Inject
    @ConfigProperty(name = "ORDER_SERVICE_HOSTNAME", defaultValue = "localhost")
    private String hostname;

    @Inject
    @ConfigProperty(name = "ORDER_SERVICE_PORT", defaultValue = "9081")
    private String port;

    public static URI apiUri;
    public static OrderClient orderClient;

    private void buildUri(){
        if (apiUri == null){
            try {
                apiUri = new URI("http://" + hostname + ":" + port);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            orderClient = RestClientBuilder
                    .newBuilder()
                    .baseUri(apiUri)
                    .build(OrderClient.class);
        }
    }

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAllOrders",
            summary = "Lists all of the submitted orders",
            description = "This operation retrieves all of the submitted orders " +
                    "and order details from the order database")
    @Tag(name = "Order",
            description = "Submitting and listing Orders")
    public Response getOrders(){ //TODO Return list of all orders, still have to figure out how to store orders
        buildUri();
        return orderClient.getOrders();
    }

    @GET
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Order")
    public Response getSingleOrder(@PathParam("orderId") String orderId){
        buildUri();
        return orderClient.getSingleOrder(orderId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Order")
    public Response createOrder(OrderRequest orderRequest){
        buildUri();
        //Validate OrderRequest object
        Set<ConstraintViolation<OrderRequest>> violations =
                validator.validate(orderRequest);

        if (violations.size() > 0) {
            JsonArrayBuilder messages = Json.createArrayBuilder();

            for (ConstraintViolation<OrderRequest> v : violations) {
                messages.add(v.getMessage());
            }

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(messages.build().toString())
                    .build();
        }

        String tableId = orderRequest.getTableId();

        //Send individual order requests to the Order service through the client
        for (String foodItem : orderRequest.getFoodList()) {
            Order order = new Order().setTableId(tableId).setItem(foodItem).setType(Type.FOOD);
            orderClient.createOrder(order);
        }

        for (String beverageItem : orderRequest.getBeverageList()) {
            Order order = new Order().setTableId(tableId).setItem(beverageItem).setType(Type.BEVERAGE);
            orderClient.createOrder(order);
        }

        return Response
                .status(Response.Status.OK)
                .build();
    }
}