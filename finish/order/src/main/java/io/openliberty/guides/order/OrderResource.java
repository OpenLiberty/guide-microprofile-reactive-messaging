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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.bind.Jsonb;
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

    private static Logger logger = Logger.getLogger(OrderResource.class.getName());

    private BlockingQueue<Order> foodQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Order> beverageQueue = new LinkedBlockingQueue<>();

    private AtomicInteger counter = new AtomicInteger();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("status")
    public Response getStatus() {
        return Response.ok().entity("The order service is running...\n"
                + foodQueue.size() + " food orders in the queue.\n"
                + beverageQueue.size() + " beverage orders in the queue.").build();
    }

 // tag::postOrder[]
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createOrder(OrderRequest orderRequest) {
        // validate OrderRequest
        Set<ConstraintViolation<OrderRequest>> violations = 
                validator.validate(orderRequest);

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

        // Create individual Orders from OrderRequest
        Order newOrder;
        String orderId;
        String tableId = orderRequest.getTableID();

        for (String foodItem : orderRequest.getFoodList()) {
            orderId = String.format("%04d", counter.incrementAndGet());
         // tag::foodOrder[]
            newOrder = new Order(orderId, tableId,
                    Type.FOOD, foodItem, Status.NEW);
         // end::foodOrder[]
         // tag::fOrderQueue[]
            foodQueue.add(newOrder);
         // end::fOrderQueue[]
        }

        for (String beverageItem : orderRequest.getBeverageList()) {
            orderId = String.format("%04d", counter.incrementAndGet());
         // tag::beverageOrder[]
            newOrder = new Order(orderId, tableId,
                    Type.BEVERAGE, beverageItem, Status.NEW);
         // end::beverageOrder[]
         // tag::bOrderQueue[]
            beverageQueue.add(newOrder);
         // end::bOrderQueue[]
        }

        return Response
                .status(Response.Status.OK)
                .entity(orderRequest)
                .build();
    }
 // end::postOrder[]

    // tag::OutgoingFood[]
    @Outgoing("food")
    public PublisherBuilder<String> sendFoodOrder() {
        return ReactiveStreams.generate(() -> {
            try {
            	// tag::takeF[]
                Order order = foodQueue.take();
                // end::takeF[]
                manager.addOrder(order);

                Jsonb jsonb = JsonbBuilder.create();
                String orderString = jsonb.toJson(order);

                logger.info("Sending Order " + order.getOrderID() + " with a status of " 
                + order.getStatus() + " to Kitchen: " + orderString);

                return orderString;
            } catch (Exception e) {
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
            	// tag::takeB[]
                Order order = beverageQueue.take();
                // end::takeB[]
                manager.addOrder(order);

                Jsonb jsonb = JsonbBuilder.create();
                String orderString = jsonb.toJson(order);

                logger.info("Sending Order " + order.getOrderID() + " with a status of " 
                + order.getStatus() + " to Bar: " + orderString);

                return orderString;
            } catch (Exception e) {
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
                .filter(order -> (tableId == null) 
                		|| order.getTableID().equals(tableId))
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

        logger.info("Order " + order.getOrderID() + " status updated to "
        + order.getStatus() + ": " + orderString);
    }
    // end::IncomingStatus[]
}
