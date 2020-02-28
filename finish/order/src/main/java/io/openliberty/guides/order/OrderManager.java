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
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderManager {
    private Map<String, Order> orders = Collections.synchronizedMap(new TreeMap<String, Order>());

    public void addOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public void updateStatus(String orderId, Status status) {
        Optional<Order> order = getOrder(orderId);
        if (order.isPresent()) order.get().setStatus(status);
    }

    public Optional<Order> getOrder(String orderId) {
        Order order = orders.get(orderId);
        return Optional.ofNullable(order);
    }

    public Map<String, Order> getOrders() {
        return new TreeMap<>(orders);
    }

    public void resetOrder() {
        orders.clear();
    }
}