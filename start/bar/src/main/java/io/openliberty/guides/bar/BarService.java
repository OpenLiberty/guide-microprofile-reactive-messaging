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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

@ApplicationScoped
public class BarService {

    private static Logger logger = Logger.getLogger(BarService.class.getName());

    private Executor executor = Executors.newSingleThreadExecutor();
    private Random random = new Random();
    private FlowableEmitter<Order> receivedOrders;

    // tag::bevOrderConsume[]    
    @Incoming("beverageOrderConsume")
    // end::bevOrderConsume[]
    // tag::bevOrderPublishInter[]
    @Outgoing("beverageOrderPublishStatus")
    // end::bevOrderPublishInter[]
    // tag::initBevOrder[]
    public Order receiveBeverageOrder(Order newOrder) {
        logger.info("Order " + newOrder.getOrderId() + " received as NEW");
        logger.info(newOrder.toString());
        Order order = prepareOrder(newOrder);
        executor.execute(() -> {
            prepare(5);
            order.setStatus(Status.READY);
            logger.info("Order " + order.getOrderId() + " is READY");
            logger.info(order.toString());
            receivedOrders.onNext(order);
        });
        return order;
    }
    // end::initBevOrder[]

    private Order prepareOrder(Order order) {
            prepare(10);
            Order inProgressOrder = order.setStatus(Status.IN_PROGRESS);
            logger.info("Order " + order.getOrderId() + " is IN PROGRESS");
            logger.info(order.toString());
            return inProgressOrder;
    }

    private void prepare(int sleepTime) {
        try {
            Thread.sleep((random.nextInt(5)+sleepTime) * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // tag::bevOrder[]
    @Outgoing("beverageOrderPublishStatus")
    // end::bevOrder[]
    public Publisher<Order> sendReadyOrder() {
        Flowable<Order> flowable = Flowable.<Order>create(emitter -> this.receivedOrders = emitter,
                BackpressureStrategy.BUFFER);
        return flowable;
    }
}