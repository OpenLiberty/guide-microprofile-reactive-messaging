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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
import io.openliberty.guides.models.Type;

@ApplicationScoped
@Path("/orders")
public class OrderResource {
	@Inject
	private OrderManager manager;

	private BlockingQueue<String> foodQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<String> drinkQueue = new LinkedBlockingQueue<>();

	private AtomicInteger counter = new AtomicInteger();
	//Jsonb jsonb = JsonbBuilder.create();

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{orderType}")
	public Response createOrder(@PathParam("orderType") String orderType) {
		Type type;

		try {
			type = Type.valueOf(orderType.toUpperCase());
		} catch (Exception e) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("Invalid order type.")
					.build();
		}

		String orderId = String.format("%04d", counter.incrementAndGet());

		Order order = new Order();
		order.setOrderID(orderId);
		order.setType(type);
		order.setStatus(Status.NEW);

		manager.addOrder(orderId, order);
		String o = JsonbBuilder.create().toJson(order);
		switch(type) {
		case FOOD:
			foodQueue.add(o);
			
			System.out.println(JsonbBuilder.create().fromJson(o,Order.class));
			break;
		case DRINK:
			drinkQueue.add(o);
			break;
		}

		return Response
				.status(Response.Status.OK)
				.entity(order)
				.build();
	}

	@Outgoing("food")
	public PublisherBuilder<String> sendFoodOrder() {
		return ReactiveStreams.generate(() -> {
			try {
				//Jsonb jsonb = JsonbBuilder.create();
				//String orderString = jsonb.toJson(foodQueue.take());
				//System.out.println( orderString );
				return foodQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	@Outgoing("drink")
	public PublisherBuilder<String> sendDrinkOrder() {
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
		System.out.println("In get");
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
	public void updateStatus(String stringOrder)  {
		System.out.println(" In Update Status method ");
		Order order = JsonbBuilder.create().fromJson(stringOrder,Order.class);
		manager.getOrder(order.getOrderID()).setStatus(order.getStatus());
		System.out.println(stringOrder);
	}
}
