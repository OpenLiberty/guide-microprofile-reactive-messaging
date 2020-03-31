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
package io.openliberty.guides.gateway.client;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/inventory")
@RegisterRestClient(configKey = "InventoryClient", baseUri = "http://localhost:9085")
public interface InventoryClient {

    //Get list of Job objects, processed from the new job JSON by the Job API
    @GET
    @Path("/jobs")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Inventory")
    Response getJobs();

    //Get single job by jobId
    @GET
    @Path("/job/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Inventory")
    Response getSingleJob(@PathParam("jobId") String jobId);

    //Get jobs by hostId
    @GET
    @Path("/host/{hostId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Inventory")
    Response getJobsList(@PathParam("hostId") String hostId);
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    Response resetJob();

}