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
package io.openliberty.guides.openlibertycafe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.openliberty.guides.openlibertycafe.client.StatusClient;

@ApplicationScoped
@Path("/status")
public class OpenLibertyCafeStatusResource {

    @Inject
    @RestClient
    private StatusClient statusClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAllOrders",
               summary = "List all submitted orders",
               description = "This operation retrieves all submitted orders " +
                   "and their details from the Status service.")
    @Tag(name = "Status", description = "Listing and quering orders")
    public Response getOrders(){
        return statusClient.getOrders();
    }

    @GET
    @Path("/order/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAnOrder",
               summary = "Show an order",
               description = "This operation retrieves the order " +
                   "and its details with the provided orderId from the Status service.")
    @Tag(name = "Status")
    public Response getSingleOrder(@PathParam("orderId") String orderId){
        return statusClient.getSingleOrder(orderId);
    }

    @GET
    @Path("/table/{tableId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listOrdersByTable",
               summary = "List the orders for a table",
               description = "This operation retrieves all orders " +
                   "of the provided tableId from the Status service.")
    @Tag(name = "Status")
    public Response getOrdersList(@PathParam("tableId") String tableId){
        return statusClient.getOrdersList(tableId);
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "resetOrder",
               summary = "Clear all orders",
               description = "This operation removes all orders " + 
                   "in the Status service.")
    @Tag
    public Response resetOrder(){
    	statusClient.resetOrder();
        return Response
                .status(Response.Status.OK)
                .build();
    }
}
