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

import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.Status;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JobStatusManager {

    private Map<String, Job> jobs = Collections.synchronizedMap(new TreeMap<String, Job>());

    public void addJob(Job job) {
    	if (!jobs.containsKey(job.jobId))
    		jobs.put(job.jobId, job);
    }

    public void updateStatus(String jobId, String hostId, Status status) {
        Optional<Job> job = getJob(jobId);
        if (job.isPresent()) {
        	if (job.get().hostId == null && hostId != null)
        	    job.get().hostId = hostId;
        	job.get().status = status;
        }
    }

    public Optional<Job> getJob(String jobId) {
        Job job = jobs.get(jobId);
        return Optional.ofNullable(job);
    }

    public Map<String, Job> getJobs() {
        return new TreeMap<>(jobs);
    }

    public void resetJob() {
        jobs.clear();
    }
}