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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.JobRequest;
import io.openliberty.guides.gateway.client.JobClient;

@ApplicationScoped
@Path("/jobs")
public class GatewayJobResource {

    @Inject
    private Validator validator;

    @Inject
    @RestClient
    private JobClient jobClient;

    //JobRequest object validator
    private Response validate(JobRequest jobRequest) {
        Set<ConstraintViolation<JobRequest>> violations =
                validator.validate(jobRequest);

        if (violations.size() > 0) {
            JsonArrayBuilder messages = Json.createArrayBuilder();

            for (ConstraintViolation<JobRequest> v : violations) {
                messages.add(v.getMessage());
            }

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(messages.build().toString())
                    .build();
        }
        return null;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createJob",
               summary = "Create jobs",
               description = "This operation creates jobs by using " + 
                   "a JobRequest and sends them to the System service.")
    @Tag(name = "Job")
    public Response createJob(JobRequest jobRequest) {

        //Validate JobRequest object
        Response validateResponse = validate(jobRequest);
        if (validateResponse != null){
            return validateResponse;
        }

        final List<String> jobIds = new ArrayList<String>();
        CountDownLatch countdownLatch = new CountDownLatch(jobRequest.getTaskList().size());
        
        //Send individual job requests to the Job service through the client
        for (String task : jobRequest.getTaskList()) {
            Job job = new Job(null, null, task, null);
            jobClient.createJob(job).thenAcceptAsync( r-> {
            	jobIds.add(r.readEntity(Job.class).jobId);
            	countdownLatch.countDown();
            });
        }

        // wait all asynchronous jobClient.createJob to be completed
        try {
            countdownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return Response
                .status(Response.Status.OK)
                .entity(jobIds)
                .build();
    }
}
