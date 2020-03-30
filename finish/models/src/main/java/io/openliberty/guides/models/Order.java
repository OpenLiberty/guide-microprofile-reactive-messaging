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

public class Order {
	
	private static final Jsonb jsonb = JsonbBuilder.create();
	
    public String orderId;
    public String tableId;
    public Type type;
    public String item;
    public Status status;

    public Order(String orderId,
                 String tableId,
                 Type type,
                 String item,
                 Status status){
        this.orderId = orderId;
        this.tableId = tableId;
        this.type = type;
        this.item = item;
        this.status = status;
    }

    public Order(String tableId,
                 Type type,
                 String item){
        this.tableId = tableId;
        this.type = type;
        this.item = item;
    }

    public Order(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId)
                && Objects.equals(tableId, order.tableId)
                && Objects.equals(type, order.type)
                && Objects.equals(item, order.item)
                && Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, tableId, type, item, status);
    }
    
    @Override
    public String toString() {
    	return "Order: " + jsonb.toJson(this);
    }
    
    public static class JsonbSerializer implements Serializer<Object> {
        @Override
        public byte[] serialize(String topic, Object data) {
          return jsonb.toJson(data).getBytes();
        }
    }
      
    public static class OrderDeserializer implements Deserializer<Order> {
        @Override
        public Order deserialize(String topic, byte[] data) {
            if (data == null)
                return null;
            return jsonb.fromJson(new String(data), Order.class);
        }
    }
}