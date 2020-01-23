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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Type;
import io.openliberty.guides.models.Status;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

@ApplicationScoped
@Path("/orders")
public class OrderResource {
    @Inject
    private OrderManager manager;

    private BlockingQueue<Order> foodQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Order> drinkQueue = new LinkedBlockingQueue<>();

    private AtomicInteger counter = new AtomicInteger();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(NewOrder order) {
        //Consumes an Order from the end user in a specific format. See finish/order.json as an example

        //Parses through the newOrder object and creates single orders of FOOD types
        String orderId;

        for(String foodItem : order.getFoodList()){
            orderId = String.format("%04d", counter.incrementAndGet());

            Order newFoodOrder = new Order(orderId, order.getTableID(), Type.FOOD, foodItem, Status.NEW);
            manager.addOrder(newFoodOrder);
            foodQueue.add(newFoodOrder);
        }

        for(String drinkItem : order.getDrinkList()){
            orderId = String.format("%04d", counter.incrementAndGet());

            Order newDrinkOrder = new Order(orderId, order.getTableID(), Type.DRINK, drinkItem, Status.NEW);
            manager.addOrder(newDrinkOrder);
            drinkQueue.add(newDrinkOrder);
        }

        return Response
                .status(Response.Status.OK)
                .entity(order)
                .build();
    }

    @Outgoing("food")
    public PublisherBuilder<Order> sendFoodOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                return foodQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Outgoing("drink")
    public PublisherBuilder<Order> sendDrinkOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                return drinkQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
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

    @Incoming("updateStatus")
    public void updateStatus(Order order)  {
        manager.getOrder(order.getOrderID()).setStatus(order.getStatus());
    }
}
