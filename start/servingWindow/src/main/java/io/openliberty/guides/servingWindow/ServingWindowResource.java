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
package io.openliberty.guides.servingWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
@Path("/servingWindow")
public class ServingWindowResource {

    private static Logger logger = Logger.getLogger(ServingWindowResource.class.getName());

    private List<Order> readyList = new ArrayList<Order>();
    private BlockingQueue<Order> completedQueue = new LinkedBlockingQueue<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listContents() {
        return Response
                .status(Response.Status.OK)
                .entity(readyList)
                .build();
    }

    @POST
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markOrderComplete(@PathParam("orderId") String orderId) {
        for (Order order : readyList ) {
            if (order.getOrderId().equals(orderId)) {
                order.setStatus(Status.COMPLETED);
                logger.info("Order " + orderId + " is now COMPLETE");
                logger.info(order.toString());
                completedQueue.add(order);
                readyList.remove(order);
                return Response
                        .status(Response.Status.OK)
                        .entity(order)
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Requested orderId does not exist")
                .build();
    }

    public void addReadyOrder(Order readyOrder)  {
        if (readyOrder.getStatus().equals(Status.READY)) {
            logger.info("Order " + readyOrder.getOrderId() + " is READY to be completed");
            logger.info(readyOrder.toString());
            readyList.add(readyOrder);
        }
    }

    public PublisherBuilder<Order> sendCompletedOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                return completedQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @DELETE
    public Response resetServingWindow() {
        readyList.clear();
        return Response
                .status(Response.Status.OK)
                .build();
    }

}
