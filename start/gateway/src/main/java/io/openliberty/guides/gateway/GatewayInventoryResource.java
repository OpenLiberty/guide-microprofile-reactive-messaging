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
package io.openliberty.guides.gateway;

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

import io.openliberty.guides.gateway.client.InventoryClient;

@ApplicationScoped
@Path("/inventory")
public class GatewayInventoryResource {

    @Inject
    @RestClient
    private InventoryClient inventoryClient;

    @GET
    @Path("/jobs")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAllJobs",
               summary = "List all submitted jobs",
               description = "This operation retrieves all submitted jobs " +
                   "and their details from the Inventory service.")
    @Tag(name = "Inventory", description = "Listing and quering jobs")
    public Response getJobs(){
        return inventoryClient.getJobs();
    }

    @GET
    @Path("/job/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAJob",
               summary = "Show a job",
               description = "This operation retrieves the job " +
                   "and its details with the provided jobId from the Inventory service.")
    @Tag(name = "Inventory")
    public Response getSingleJob(@PathParam("jobId") String jobId){
        return inventoryClient.getSingleJob(jobId);
    }

    @GET
    @Path("/host/{hostId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listJobsByHost",
               summary = "List the jobs for a host",
               description = "This operation retrieves all jobs " +
                   "of the provided hostId from the Inventory service.")
    @Tag(name = "Inventory")
    public Response getJobsList(@PathParam("hostId") String hostId){
        return inventoryClient.getJobsList(hostId);
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "resetJob",
               summary = "Clear all jobs",
               description = "This operation removes all jobs " + 
                   "in the Inventory service.")
    @Tag
    public Response resetJob(){
    	inventoryClient.resetJob();
        return Response
                .status(Response.Status.OK)
                .build();
    }
}
