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

public class Order {
    private String orderID;
    private String tableID;
    private Type type;
    private String item;
    private Status status;

    public Order(String orderID,
                 String tableID,
                 Type type,
                 String item,
                 Status status){
        this.orderID = orderID;
        this.tableID = tableID;
        this.type = type;
        this.item = item;
        this.status = status;
    }

    public Order(){

    }

    public String getTableID() {
        return tableID;
    }

    public Order setTableID(String tableID) {
        this.tableID = tableID;
        return this;
    }

    public String getItem() {
        return item;
    }

    public Order setItem(String item) {
        this.item = item;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Order setType(Type type) {
        this.type = type;
        return this;
    }

    public String getOrderID() {
        return orderID;
    }

    public Order setOrderID(String orderID) {
        this.orderID = orderID;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Order setStatus(Status status) {
        this.status = status;
        return this;
    }
    
}