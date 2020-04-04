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
package io.openliberty.guides.models;

import java.util.Objects;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

public class Job {
	
	private static final Jsonb jsonb = JsonbBuilder.create();
	
    public String jobId;
    public String hostId;
    public String taskDesc;
    public Status status;

    public Job(String jobId,
                 String hostId,
                 String taskDesc,
                 Status status){
        this.jobId = jobId;
        this.hostId = hostId;
        this.taskDesc = taskDesc;
        this.status = status;
    }

    public Job() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job job = (Job) o;
        return Objects.equals(jobId, job.jobId)
                && Objects.equals(hostId, job.hostId)
                && Objects.equals(taskDesc, job.taskDesc)
                && Objects.equals(status, job.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, hostId, taskDesc, status);
    }
    
    @Override
    public String toString() {
    	return "Job: " + jsonb.toJson(this);
    }
    
    public static class JsonbSerializer implements Serializer<Object> {
        @Override
        public byte[] serialize(String topic, Object data) {
          return jsonb.toJson(data).getBytes();
        }
    }
      
    public static class JobDeserializer implements Deserializer<Job> {
        @Override
        public Job deserialize(String topic, byte[] data) {
            if (data == null)
                return null;
            return jsonb.fromJson(new String(data), Job.class);
        }
    }
}