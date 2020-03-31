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

import io.openliberty.guides.models.Job;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

@Path("/jobs")
@RegisterRestClient(configKey = "JobClient", baseUri = "http://localhost:9081")
public interface JobClient {

    //Sends each job to Job API for processing
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Job")
    @Asynchronous
    CompletionStage<Response> createJob(Job job);

    //Get list of Job objects, processed from the new job JSON by the Job API
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Job")
    Response getJobs();

    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    @Tag(name = "Job")
    Response getStatus();
}