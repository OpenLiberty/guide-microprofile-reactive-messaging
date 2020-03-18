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
public class KitchenService {

    private static Logger logger = Logger.getLogger(KitchenService.class.getName());

    private Executor executor = Executors.newSingleThreadExecutor();
    private Random random = new Random();
    private FlowableEmitter<Order> receivedOrders;

    // tag::foodOrderConsume[]
    @Incoming("foodOrderConsume")
    // end::foodOrderConsume[]
    // tag::foodOrderPublishIntermediate[]
    @Outgoing("foodOrderPublishStatus")
    // end::foodOrderPublishIntermediate[]
    // tag::initFoodOrder[]
    public Order receiveFoodOrder(Order newOrder) {
        logger.info("Order " + newOrder.getOrderId() + " received with a status of NEW");
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
    // end::initFoodOrder[]

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
    
    // tag::foodOrder[]
    @Outgoing("foodOrderPublishStatus")
   // end::foodOrder[]
    public Publisher<Order> sendReadyOrder() {
        Flowable<Order> flowable = Flowable.<Order>create(emitter -> 
        this.receivedOrders = emitter, BackpressureStrategy.BUFFER);
        return flowable;
    }
}
