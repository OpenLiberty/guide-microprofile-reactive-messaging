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
package io.openliberty.guides.restaurantbff.client;

import io.openliberty.guides.models.Order;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface OrderClient {

    //Sends each order to Order API for processing
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Order")
    Response createOrder(Order order);

    //Get list of Order objects, processed from the new order JSON by the Order API
    @GET
    @Tag(name = "Order")
    Response getOrders();

    //Get single order by orderId
    @GET
    @Path("/{orderId}")
    @Tag(name = "Order")
    Response getSingleOrder(@PathParam("orderId") String orderId);

}