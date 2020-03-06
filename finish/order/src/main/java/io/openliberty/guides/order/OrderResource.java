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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

@ApplicationScoped
@Path("/orders")
public class OrderResource {

    private static Logger logger = Logger.getLogger(OrderResource.class.getName());

    private BlockingQueue<Order> foodQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Order> beverageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Order> statusQueue = new LinkedBlockingQueue<>();

    private AtomicInteger counter = new AtomicInteger();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("status")
    public Response getStatus() {
        return Response
                .status(Response.Status.OK)
                .entity("The order service is running...\n"
                        + foodQueue.size() + " food orders in the queue.\n"
                        + beverageQueue.size() + " beverage orders in the queue.")
                .build();
    }

    // tag::postOrder[]
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/")
    // tag::createOrder[]
    public Response createOrder(Order order) {
        order.setOrderId(String.format("%04d", counter.incrementAndGet()))
                .setStatus(Status.NEW);

        switch(order.getType()){
            // tag::foodOrder[]
            case FOOD:
                // end::foodOrder[]
                // tag::fOrderQueue[]
                foodQueue.add(order);
                // end::fOrderQueue[]
                break;
            // tag::beverageOrder[]
            case BEVERAGE:
                // end::beverageOrder[]
                // tag::bOrderQueue[]
                beverageQueue.add(order);
                // end::bOrderQueue[]
                break;
        }

        return Response
                .status(Response.Status.OK)
                .entity(order)
                .build();
    }
    // end::createOrder[]
    // end::postOrder[]

    // tag::OutgoingFood[]
    @Outgoing("food")
    // end::OutgoingFood[]
    public PublisherBuilder<Order> sendFoodOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                // tag::takeF[]
                Order order = foodQueue.take();
                // end::takeF[]
                statusQueue.add(order);
                logger.info("Sending Order " + order.getOrderId() + " with a status of "
                        + order.getStatus() + " to Kitchen: " + order.toString());
                return order;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }


    // tag::OutgoingBev[]
    @Outgoing("beverage")
    // end::OutgoingBev[]
    public PublisherBuilder<Order> sendBeverageOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                // tag::takeB[]
                Order order = beverageQueue.take();
                // end::takeB[]
                statusQueue.add(order);
                logger.info("Sending Order " + order.getOrderId() + " with a status of "
                        + order.getStatus() + " to Bar: " + order.toString());
                return order;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Outgoing("updateStatus")
    public PublisherBuilder<Order> updateStatus() {
        return ReactiveStreams.generate(() -> {
            try {
                // tag::takeC[]
                Order order = statusQueue.take();
                // end::takeC[]
                logger.info("Sending Order " + order.getOrderId() + " with a status of "
                        + order.getStatus() + " to Status: " + order.toString());
                return order;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
	}
}
