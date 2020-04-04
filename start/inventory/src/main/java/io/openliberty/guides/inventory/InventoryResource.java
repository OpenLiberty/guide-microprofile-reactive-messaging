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
package io.openliberty.guides.inventory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.openliberty.guides.models.Job;

@ApplicationScoped
@Path("/inventory")
public class InventoryResource {

    private static Logger logger = Logger.getLogger(InventoryResource.class.getName());

    @Inject
    private JobStatusManager manager;

    @GET
    @Path("/jobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobsList() {
        List<Job> jobList = manager.getJobs()
                .values()
                .stream()
                .collect(Collectors.toList());
        return Response
                .status(Response.Status.OK)
                .entity(jobList)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/job/{jobId}")
    public Response getJob(@PathParam("jobId") String jobId) {
        Optional<Job> job = manager.getJob(jobId);
        if (job.isPresent()) {
            return Response
                    .status(Response.Status.OK)
                    .entity(job)
                    .build();
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Job id does not exist.")
                .build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/host/{hostId}")
    public Response getJobsList(@PathParam("hostId") String hostId) {
        List<Job> jobsList = manager.getJobs()
                .values()
                .stream()
                .filter(job -> job.hostId.equals(hostId))
                .collect(Collectors.toList());
        return Response
                .status(Response.Status.OK)
                .entity(jobsList)
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetJob() {
        manager.resetJob();
        return Response
                .status(Response.Status.OK)
                .build();
    }
    
    // tag::updateStatus[]
    @Incoming("updateStatus")
    // end::updateStatus[]
    public void updateStatus(Job job)  {
        String jobId = job.jobId;
        if (manager.getJob(jobId).isPresent()) {
            manager.updateStatus(jobId, job.hostId, job.status);
            logger.info("Job " + jobId + " status updated to "
                + job.status + ": " + job);
        } else {
            manager.addJob(job);
            logger.info("Job " + jobId + " was added: " + job);	
        }
    }
}
