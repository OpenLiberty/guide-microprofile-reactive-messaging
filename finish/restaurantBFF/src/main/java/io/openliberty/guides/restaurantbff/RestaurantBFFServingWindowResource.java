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

import io.openliberty.guides.restaurantbff.client.ServingWindowClient;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/servingWindow")
public class RestaurantBFFServingWindowResource {

    @Inject
    @RestClient
    private ServingWindowClient servingWindowClient;

    //Returns list of all ready orders
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Serving Window",
            description = "Listing and completing 'READY' Orders")
    public Response getReady2Serve(){
        return servingWindowClient.getReady2Serve();
    }

    //Completes a ready order of a particular orderId
    @POST
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Serving Window")
    public Response serveOrder(@PathParam("orderId") String orderId) {
        return servingWindowClient.serveOrder(orderId);
    }
}