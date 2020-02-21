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
package io.openliberty.guides.kitchen;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

@ApplicationScoped
@Path("/foodMessaging")
public class KitchenResource {

    private static Logger logger = Logger.getLogger(KitchenResource.class.getName());
    private static Jsonb jsonb = JsonbBuilder.create();

    private Executor executor = Executors.newSingleThreadExecutor();
    private BlockingQueue<Order> inProgress = new LinkedBlockingQueue<>();
    private Random random = new Random();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStatus() {
        return Response.ok().entity("The kitchen service is running...\n"
                + inProgress.size() + " orders in the queue.").build();
    }

    // tag::foodOrderConsume[]
    @Incoming("foodOrderConsume")
    // end::foodOrderConsume[]
    // tag::foodOrderPublishIntermediate[]
    @Outgoing("foodOrderPublishIntermediate")
    // end::foodOrderPublishIntermediate[]
    // tag::initFoodOrder[]
    public CompletionStage<String> initFoodOrder(String newOrder) {
        Order order = jsonb.fromJson(newOrder, Order.class);
        logger.info("Order " + order.getOrderId() + " received with a status of NEW");
        logger.info(newOrder);
        return prepareOrder(order).thenApply(Order -> jsonb.toJson(Order));
    }
    // end::initFoodOrder[]

    // tag::foodOrder[]
    @Outgoing("foodOrderPublish")
    // end::foodOrder[]
    public PublisherBuilder<String> sendReadyOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                Order order = inProgress.take();
                prepare(5);
                order.setStatus(Status.READY);
                String orderString = jsonb.toJson(order);
                logger.info("Order " + order.getOrderId() + " is READY");
                logger.info(orderString);
                return orderString;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
    
    
    private CompletionStage<Order> prepareOrder(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            prepare(10);
            Order inProgressOrder = order.setStatus(Status.IN_PROGRESS);
            logger.info("Order " + order.getOrderId() + " is IN PROGRESS");
            logger.info(jsonb.toJson(order));
            inProgress.add(inProgressOrder);
            return inProgressOrder;
        }, executor);
    }

    private void prepare(int sleepTime) {
        try {
            Thread.sleep((random.nextInt(5)+sleepTime) * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
