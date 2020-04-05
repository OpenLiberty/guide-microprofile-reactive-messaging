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

public class CpuUsage {

    private static final Jsonb jsonb = JsonbBuilder.create();

    public String hostId;
    public Double cpuUsage;
        
    public CpuUsage(String hostId, Double cpuUsage) {
        this.hostId = hostId;
        this.cpuUsage = cpuUsage;
    }

    public CpuUsage() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CpuUsage)) return false;
        CpuUsage c = (CpuUsage) o;
        return Objects.equals(hostId, c.hostId)
                && Objects.equals(cpuUsage, c.cpuUsage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostId, cpuUsage);
    }
    
    @Override
    public String toString() {
        return "CpuUsage: " + jsonb.toJson(this);
    }
    
    public static class JsonbSerializer implements Serializer<Object> {
        @Override
        public byte[] serialize(String topic, Object data) {
          return jsonb.toJson(data).getBytes();
        }
    }
      
    public static class CpuUsageDeserializer implements Deserializer<CpuUsage> {
        @Override
        public CpuUsage deserialize(String topic, byte[] data) {
            if (data == null)
                return null;
            return jsonb.fromJson(new String(data), CpuUsage.class);
        }
    }
}