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
package io.openliberty.guides.bar;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

@ApplicationScoped
public class BarService {

    private static Logger logger = Logger.getLogger(BarService.class.getName());

    private Executor executor = Executors.newSingleThreadExecutor();
    private BlockingQueue<Order> inProgress = new LinkedBlockingQueue<>();
    private Random random = new Random();

    // tag::bevOrderConsume[]    
    @Incoming("bevOrderConsume")
    // end::bevOrderConsume[]
    // tag::bevOrderPublishInter[]
    @Outgoing("bevOrderPublishInter")
    // end::bevOrderPublishInter[]
    // tag::initBevOrder[]
    public CompletionStage<Order> initBeverageOrder(Order newOrder) {
        logger.info("Order " + newOrder.getOrderId() + " received as NEW");
        logger.info(newOrder.toString());
        return prepareOrder(newOrder);
    }
    // end::initBevOrder[]

    // tag::bevOrder[]
    @Outgoing("beverageOrderPublish")
    // end::bevOrder[]
    public PublisherBuilder<Order> sendReadyOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                Order order = inProgress.take();
                prepare(5);
                order.setStatus(Status.READY);
                logger.info("Order " + order.getOrderId() + " is READY");
                logger.info(order.toString());
                return order;
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
            logger.info(order.toString());
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
