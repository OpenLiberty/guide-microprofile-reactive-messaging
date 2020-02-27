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

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/servingWindow")
@RegisterRestClient(configKey = "ServingWindowClient", baseUri = "http://localhost:9082")
public interface ServingWindowClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Serving Window")
    Response getReady2Serve();

    @POST
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Serving Window")
    Response serveOrder(@PathParam("orderId") String orderId);

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    Response resetServingWindow();

}