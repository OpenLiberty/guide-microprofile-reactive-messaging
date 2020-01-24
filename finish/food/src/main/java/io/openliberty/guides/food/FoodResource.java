package io.openliberty.guides.food;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
// JAX-RS
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

/**
 * 
 * Food Microservice using Eclipse
 * microprofile reactive messaging 
 * running on Open Liberty
 * 
 */
@ApplicationScoped
@Path("/foodMessaging")
public class FoodResource {

	private Executor executor = Executors.newSingleThreadExecutor();
	private BlockingQueue<Order> inProgress = new LinkedBlockingQueue<>();
	Jsonb jsonb = JsonbBuilder.create();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getProperties() {
		return Response.ok().entity(" In food service ").build();
	}

	/**
	 * Food message Order processor
	 * @param newOrder
	 * @return CompletionStage<String>
	 */
	@Incoming("foodOrderConsume")
	@Outgoing("foodOrderPublishIntermediate")
	public CompletionStage<String> initFoodOrder(String newOrder) {
		System.out.println("\n New Food Order received ");
		System.out.println( " Order : " + newOrder);
		Order order = jsonb.fromJson(newOrder, Order.class);
		return prepareOrder(order).thenApply(Order -> jsonb.toJson(Order));
	}

	private CompletionStage<Order> prepareOrder(Order order) {
		return CompletableFuture.supplyAsync(() -> {
			prepare(4000);
			System.out.println(" Food Order in Progress... ");
			Order inProgressOrder = order.setStatus(Status.IN_PROGRESS);
			System.out.println(  " Order : " + jsonb.toJson(inProgressOrder) );
			inProgress.add(inProgressOrder);
			return inProgressOrder;
		}, executor);
	}

	private void prepare(long milliSec) {
		try {
			Thread.sleep(milliSec);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Publish Ready Food Order message to Kafka
	 * @return PublisherBuilder<String>
	 */
	@Outgoing("foodOrderPublish")
	public PublisherBuilder<String> sendReadyOrder() {
		return ReactiveStreams.generate(() -> {
			try {
				Order order = inProgress.take();
				prepare(3000);
				order.setStatus(Status.READY);
				System.out.println(" Food Order Ready... ");
				System.out.println(  " Order : " + jsonb.toJson(order) );
				return jsonb.toJson(order);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		});
	}
}
