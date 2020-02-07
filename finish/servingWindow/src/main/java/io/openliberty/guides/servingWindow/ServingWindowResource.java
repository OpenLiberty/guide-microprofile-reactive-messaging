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

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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

    private List<Order> readyList = new ArrayList<Order>();
    private BlockingQueue<String> completedQueue = new LinkedBlockingQueue<>();
    Jsonb jsonb = JsonbBuilder.create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listContents() {
        return Response
                .status(Response.Status.OK)
                .entity(readyList)
                .build();
    }

    @POST
    @Path("/complete/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markOrderComplete( @PathParam("orderId") String orderId ) {
        for ( Order order : readyList ) {
            if ( order.getOrderID().equals(orderId)) {
                System.out.println( "\n Marking Order : " + orderId +
                		" as Completed... ");
                order.setStatus(Status.COMPLETED);
                System.out.println(  " Order : " + jsonb.toJson(order) );
                completedQueue.add(jsonb.toJson(order));
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

    @Incoming("orderReady")
    public void addReadyOrder(String readyOrder)  {
        Order order = JsonbBuilder.create().fromJson(readyOrder,Order.class);
        if ( order.getStatus().equals(Status.READY))
            readyList.add(order);
    }

    @Outgoing("completedOrder")
    public PublisherBuilder<String> sendCompletedOrder() {
        return ReactiveStreams.generate(() -> {
            try {
                return completedQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
