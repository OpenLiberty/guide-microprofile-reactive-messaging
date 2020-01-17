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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Type;
import io.openliberty.guides.models.Status;

@ApplicationScoped
@Path("/orders")
public class OrderResource {
    @Inject
    private OrderManager manager;

    private AtomicInteger counter = new AtomicInteger();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/drink")
    public Response orderDrink() {
        String orderId = String.format("%04d", counter.incrementAndGet());

        Order order = new Order();
        order.setOrderID(orderId);
        order.setType(Type.DRINK);
        order.setStatus(Status.NEW);

        manager.addOrder(orderId, order);

        return Response
                .status(Response.Status.OK)
                .entity(order)
                .build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/food")
    public Response orderFood() {
        String orderId = String.format("%04d", counter.incrementAndGet());

        Order order = new Order();
        order.setOrderID(orderId);
        order.setType(Type.FOOD);
        order.setStatus(Status.NEW);

        manager.addOrder(orderId, order);

        return Response
                .status(Response.Status.OK)
                .entity(order)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{orderId}")
    public Response getStatus(@PathParam("orderId") String orderId) {
        Order order = manager.getOrder(orderId);

        if (order == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Order id does not exist.")
                    .build();
        }

        return Response
                .status(Response.Status.OK)
                .entity(order.getStatus())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getOrdersList() {
        List<Order> ordersList = manager.getOrders()
                                        .entrySet()
                                        .stream()
                                        .map(es -> es.getValue())
                                        .collect(Collectors.toList());

        return Response
                .status(Response.Status.OK)
                .entity(ordersList)
                .build();
    }
}
