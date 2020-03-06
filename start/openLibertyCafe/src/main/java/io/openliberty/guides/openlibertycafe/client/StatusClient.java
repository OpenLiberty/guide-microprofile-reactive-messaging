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
package io.openliberty.guides.openlibertycafe.client;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/status")
@RegisterRestClient(configKey = "StatusClient", baseUri = "http://localhost:9085")
public interface StatusClient {

    //Get list of Order objects, processed from the new order JSON by the Order API
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Status")
    Response getOrders();

    //Get single order by orderId
    @GET
    @Path("/order/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Status")
    Response getSingleOrder(@PathParam("orderId") String orderId);

    //Get orders by tableId
    @GET
    @Path("/table/{tableId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Status")
    Response getOrdersList(@PathParam("tableId") String tableId);
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    Response resetOrder();

}