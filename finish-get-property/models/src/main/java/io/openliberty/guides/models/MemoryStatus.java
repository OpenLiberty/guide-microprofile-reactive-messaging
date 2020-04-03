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

public class MemoryStatus {

    private static final Jsonb jsonb = JsonbBuilder.create();

    public String hostId;
    public Long memoryUsed;
    public Long memoryMax;
    
    public MemoryStatus(String hostId, Long memoryUsed, Long memoryMax) {
        this.hostId = hostId;
        this.memoryUsed = memoryUsed;
        this.memoryMax = memoryMax;
    }

    public MemoryStatus() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemoryStatus)) return false;
        MemoryStatus m = (MemoryStatus) o;
        return Objects.equals(hostId, m.hostId)
                && Objects.equals(memoryUsed, m.memoryUsed)
                && Objects.equals(memoryMax, m.memoryMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostId, memoryUsed, memoryMax);
    }
    
    @Override
    public String toString() {
        return "MemoryUsage: " + jsonb.toJson(this);
    }
    
    public static class JsonbSerializer implements Serializer<Object> {
        @Override
        public byte[] serialize(String topic, Object data) {
          return jsonb.toJson(data).getBytes();
        }
    }
      
    public static class MemoryStatusDeserializer implements Deserializer<MemoryStatus> {
        @Override
        public MemoryStatus deserialize(String topic, byte[] data) {
            if (data == null)
                return null;
            return jsonb.fromJson(new String(data), MemoryStatus.class);
        }
    }
}