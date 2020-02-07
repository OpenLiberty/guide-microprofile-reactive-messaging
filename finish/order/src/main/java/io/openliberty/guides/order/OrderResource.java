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
import io.openliberty.guides.models.OrderRequest;
import io.openliberty.guides.models.Status;
import io.openliberty.guides.models.Type;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.bind.JsonbBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

@ApplicationScoped
@Path("/orders")
public class OrderResource {
    @Inject
    private OrderManager manager;

    @Inject
    private Validator validator;

    private BlockingQueue<Order> foodQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Order> beverageQueue = new LinkedBlockingQueue<>();

    private AtomicInteger counter = new AtomicInteger();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createOrder(OrderRequest orderRequest) {
        // validate OrderRequest
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);

        if (violations.size() > 0) {
            JsonArrayBuilder messages = Json.createArrayBuilder();

            for (ConstraintViolation<OrderRequest> v : violations) {
                messages.add(v.getMessage());
            }

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(messages.build().toString())
                    .build();
        }

        // Create invdividual Orders from OrderRequest
        String orderId;
        Order newOrder;

        for (String foodItem : orderRequest.getFoodList()) {
            orderId = String.format("%04d", counter.incrementAndGet());

            newOrder = new Order(orderId, orderRequest.getTableID(), Type.FOOD, foodItem, Status.NEW);
            manager.addOrder(newOrder);
            foodQueue.add(newOrder);
        }

        for (String beverageItem : orderRequest.getBeverageList()) {
            orderId = String.format("%04d", counter.incrementAndGet());

            newOrder = new Order(orderId, orderRequest.getTableID(), Type.BEVERAGE, beverageItem, Status.NEW);
            manager.addOrder(newOrder);
            beverageQueue.add(newOrder);
        }

        return Response
                .status(Response.Status.OK)
                .entity(orderRequest)
                .build();
    }
    // tag::OutgoingFood[]
    @Outgoing("food")
    public PublisherBuilder<String> sendFoodOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                return JsonbBuilder.create().toJson(foodQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    // end::OutgoingFood[]

    // tag::OutgoingBev[]
    @Outgoing("beverage")
    public PublisherBuilder<String> sendBeverageOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                return JsonbBuilder.create().toJson(beverageQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    // end::OutgoingBev[]

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{orderId}")
    public Response getOrder(@PathParam("orderId") String orderId) {
        Order order = manager.getOrder(orderId);

        if (order == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Order id does not exist.")
                    .build();
        }

        return Response
                .status(Response.Status.OK)
                .entity(order)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getOrdersList(@QueryParam("tableId") String tableId) {
        List<Order> ordersList = manager.getOrders()
                .values()
                .stream()
                .filter(order -> (tableId == null) || order.getTableID().equals(tableId))
                .collect(Collectors.toList());

        return Response
                .status(Response.Status.OK)
                .entity(ordersList)
                .build();
    }

    // tag::IncomingStatus[]
    @Incoming("updateStatus")
    public void updateStatus(String orderString)  {
        Order order = JsonbBuilder.create().fromJson(orderString, Order.class);
        manager.updateStatus(order.getOrderID(), order.getStatus());
        System.out.println("Updated Order " + order.getOrderID() + " status to " + order.getStatus());
    }
    // end::IncomingStatus[]

}
