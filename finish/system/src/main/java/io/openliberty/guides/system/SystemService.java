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
package io.openliberty.guides.system;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.Status;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

@ApplicationScoped
public class SystemService {

    private static Logger logger = Logger.getLogger(SystemService.class.getName());

    private Executor executor = Executors.newSingleThreadExecutor();
    private Random random = new Random();
    private FlowableEmitter<Job> receivedJobs;

    // tag::jobConsume[]
    @Incoming("jobConsume")
    // end::jobConsume[]
    // tag::jobPublishIntermediate[]
    @Outgoing("jobPublishStatus")
    // end::jobPublishIntermediate[]
    // tag::initJob[]
    public Job receiveJob(Job newJob) {
        logger.info("Job " + newJob.jobId + " received with a status of NEW");
        logger.info(newJob.toString());
        Job job = prepareJob(newJob);
        executor.execute(() -> {
            prepare(5);
            job.status = Status.COMPLETED;
            logger.info("Job " + job.jobId + " is COMPLETED");
            logger.info(job.toString());
            receivedJobs.onNext(job);
        });
        return job;
    }
    // end::initJob[]

    private Job prepareJob(Job job) {
        prepare(10);
        job.hostId = System.getenv("HOSTNAME");
        job.status = Status.IN_PROGRESS;
        logger.info("Job " + job.jobId + " is IN PROGRESS");
        logger.info(job.toString());
        return job;
    }

    private void prepare(int sleepTime) {
        try {
            Thread.sleep((random.nextInt(5)+sleepTime) * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // tag::jobPublishStatus[]
    @Outgoing("jobPublishStatus")
    // end::jobPublishStatus[]
    public Publisher<Job> sendCompletedJob() {
        Flowable<Job> flowable = Flowable.<Job>create(emitter -> 
            this.receivedJobs = emitter, BackpressureStrategy.BUFFER);
        return flowable;
    }
}
