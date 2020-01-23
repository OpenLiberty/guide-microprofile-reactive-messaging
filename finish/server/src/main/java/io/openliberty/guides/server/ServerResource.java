package io.openliberty.guides.server;

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

/**
 * 
 * FoodServing Microservice using Eclipse
 * microprofile reactive messaging 
 * running on Open Liberty
 *
 */
@ApplicationScoped
@Path("/server")
public class ServerResource {

	private List<Order> readyList = new ArrayList<Order>();
	private BlockingQueue<String> completedQueue = new LinkedBlockingQueue<>();
	Jsonb jsonb = JsonbBuilder.create();

	/**
	 * Get list of all Ready Orders
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listContents() {
		return Response
				.status(Response.Status.OK)
				.entity(readyList)
				.build();
	}

	/**
	 * Mark Order as completed
	 * @param orderId
	 */
	@POST
	@Path("/complete/{orderId}")
	public void markOrderComplete( @PathParam("orderId") String orderId ) {
		for ( Order order : readyList ) {
			if ( order.getOrderID().equals(orderId)) {
				System.out.println( "\n Marking Order : " + orderId + " as Completed... ");
				order.setStatus(Status.COMPLETED);
				System.out.println(  " Order : " + jsonb.toJson(order) );
				completedQueue.add(jsonb.toJson(order));
			}
		}
	}

	/**
	 * Consume Ready Order
	 * @param readyOrder
	 */
	@Incoming("orderReady")
	public void addReadyOrder(String readyOrder)  {
		Order order = JsonbBuilder.create().fromJson(readyOrder,Order.class);
		if ( order.getStatus().equals(Status.READY))
			readyList.add(order);
	}

	/**
	 * Publish Completed Order
	 * @return PublisherBuilder<String>
	 */
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
