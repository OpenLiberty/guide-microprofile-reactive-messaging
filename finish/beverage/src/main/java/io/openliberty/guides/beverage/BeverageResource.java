package io.openliberty.guides.beverage;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

@ApplicationScoped
@Path("/beverageMessaging")
public class BeverageResource {

	private Random random = new Random();
	private Executor executor = Executors.newSingleThreadExecutor();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProperties() {
		return Response.ok("In beverage Service")
				.build();
	}

	@Incoming("beverageOrderConsume")
	@Outgoing("beverageOrderPublish")
	public CompletionStage<Order> initBeverageOrder(String newOrder) {
		Jsonb jsonb = JsonbBuilder.create();
		Order order = jsonb.fromJson(newOrder, Order.class);
		System.out.println(" Beverage Order is being prepared...");
		return prepareOrder(order);
	}

	private CompletionStage<Order> prepareOrder(Order order) {
		return CompletableFuture.supplyAsync(() -> {
			order.setStatus(Status.IN_PROGRESS);
			System.out.println(" Beverage Order in Progress... ");
			prepare();
			return order.setStatus(Status.READY);
		}, executor);
	}

	private void prepare() {
		try {
			Thread.sleep(random.nextInt(5000));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
