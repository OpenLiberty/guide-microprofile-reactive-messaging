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
package io.openliberty.guides.order;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderManager {
    private Map<String, Order> orders = Collections.synchronizedMap(new HashMap<String, Order>());

    public void addOrder(Order order) {
        orders.put(order.getOrderID(), order);
    }

    public void updateStatus(String orderId, Status status) {
        orders.get(orderId).setStatus(status);
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public Map<String, Order> getOrders() {
        return new HashMap<>(orders);
    }

 }