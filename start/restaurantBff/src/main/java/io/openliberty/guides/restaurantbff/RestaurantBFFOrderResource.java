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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.OrderRequest;
import io.openliberty.guides.restaurantbff.client.OrderClient;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/orders")
public class RestaurantBFFOrderResource {

    @Inject
    private OrderClient orderClient;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAllOrders",
            summary = "Lists all of the submitted orders",
            description = "This operation retrieves all of the submitted orders " +
                    "and order details from the order database")
    @Tag(name = "Order",
            description = "Submitting and listing Orders")
    public Response getOrders(){ //TODO Return list of all orders, still have to figure out how to store orders
        return orderClient.getOrders();
    }

    @GET
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Order")
    public Response getSingleOrder(@PathParam("orderId") String orderId){
        return orderClient.getSingleOrder(orderId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Order")
    public Response createOrder(OrderRequest orderRequest){
        return orderClient.createOrder(orderRequest);
    }
}