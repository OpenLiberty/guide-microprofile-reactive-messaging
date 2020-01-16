package io.openliberty.guides.food;

// CDI
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
// JAX-RS
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@RequestScoped
@Path("/properties") // may be messaging
public class FoodResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProperties() {
		return Response.ok("Food Properties")
				.build();
	}

	@Outgoing("foodOrderPublish")
	public String sendMessage() {
		System.out.println(" Ready to Serve");
		return ("Order Status");

	}

	@Incoming("foodOrderConsume")
	public void consumeOrder( String order ) {//Order o
		System.out.println(" Consuming Order");
		// do processing
	}
}
