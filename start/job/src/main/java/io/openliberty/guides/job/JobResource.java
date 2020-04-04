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
package io.openliberty.guides.job;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.Status;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

@ApplicationScoped
@Path("/jobs")
public class JobResource {

    private static Logger logger = Logger.getLogger(JobResource.class.getName());

    private FlowableEmitter<Job> task;
    private FlowableEmitter<Job> statusUpdate;

    private AtomicInteger counter = new AtomicInteger();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("status")
    public Response getStatus() {
        return Response
                .status(Response.Status.OK)
                .entity("The job service is running...\n")
                .build();
    }

    // tag::postJob[]
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/")
    // tag::createJob[]
    public Response createJob(Job job) {
    	job.jobId = String.format("%04d", counter.incrementAndGet());
        job.status = Status.NEW;

        logger.info("Sending Job " + job.jobId + " with a status of "
                        + job.status + " to System: " + job.toString());
        // tag::jobQueue[]
        task.onNext(job);
        // end::jobQueue[]

        statusUpdate.onNext(job);
        return Response
                .status(Response.Status.OK)
                .entity(job)
                .build();
    }
    // end::createJob[]
    // end::postJob[]

    // tag::OutgoingJob[]
    @Outgoing("job")
    // end::OutgoingJob[]
    public Publisher<Job> sendJob() {
        // tag::takeF[]
        Flowable<Job> flowable = Flowable.<Job>create(emitter -> 
            this.task = emitter, BackpressureStrategy.BUFFER);
        // end::takeF[]
        return flowable;
    }
    
    @Outgoing("updateStatus")
     public Publisher<Job> updateStatus() {
         Flowable<Job> flowable = Flowable.<Job>create(emitter -> 
         this.statusUpdate = emitter, BackpressureStrategy.BUFFER)
                 .doAfterNext( job -> logger.info("Sending Job "
         + job.jobId + " with a status of " + job.status
         + " to Status: " + job.toString()));
         return flowable;
     }
}