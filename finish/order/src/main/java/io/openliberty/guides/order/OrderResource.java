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
import org.reactivestreams.Publisher;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

@ApplicationScoped
@Path("/orders")
public class OrderResource {

    private static Logger logger = Logger.getLogger(OrderResource.class.getName());

    private FlowableEmitter<Order> foodItem;
    private FlowableEmitter<Order> beverageItem;
    private FlowableEmitter<Order> statusUpdate;

    private AtomicInteger counter = new AtomicInteger();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("status")
    public Response getStatus() {
        return Response
                .status(Response.Status.OK)
                .entity("The order service is running...\n")
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
                logger.info("Sending Order " + order.getOrderId() + " with a status of "
                        + order.getStatus() + " to Kitchen: " + order.toString());
                // tag::fOrderQueue[]
                foodItem.onNext(order);
                // end::fOrderQueue[]
                break;
            // tag::beverageOrder[]
            case BEVERAGE:
                // end::beverageOrder[]
                logger.info("Sending Order " + order.getOrderId() + " with a status of "
                        + order.getStatus() + " to Bar: " + order.toString());
                // tag::bOrderQueue[]
                beverageItem.onNext(order);
                // end::bOrderQueue[]
                break;
        }
        statusUpdate.onNext(order);
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
    public Publisher<Order> sendFoodOrder() {
        // tag::takeF[]
        Flowable<Order> flowable = Flowable.<Order>create(emitter -> 
        this.foodItem = emitter, BackpressureStrategy.BUFFER);
        // end::takeF[]
        return flowable;
    }
    
    // tag::OutgoingBev[]
    @Outgoing("beverage")
    // end::OutgoingBev[]
    public Publisher<Order> sendBeverageOrder() {
        // tag::takeB[]
        Flowable<Order> flowable = Flowable.<Order>create(emitter -> 
        this.beverageItem = emitter, BackpressureStrategy.BUFFER);
        // end::takeB[]
        return flowable;
    }
    
    @Outgoing("updateStatus")
     public Publisher<Order> updateStatus() {
        System.out.println("In updateStatus");
         Flowable<Order> flowable = Flowable.<Order>create(emitter -> 
         this.statusUpdate = emitter, BackpressureStrategy.BUFFER)
                 .doAfterNext( order -> logger.info("Sending Order "
         + order.getOrderId() + " with a status of " + order.getStatus() 
         + " to Status: " + order.toString()));
         return flowable;
     }
}
