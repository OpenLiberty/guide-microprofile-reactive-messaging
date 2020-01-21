package io.openliberty.guides.foodServing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Status;

@RequestScoped
@Path("/status")
public class FoodServingResource {

	private BlockingQueue<Order> readyToServeList = new LinkedBlockingQueue<>();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BlockingQueue<Order> listContents() {
		//readyToServeList.forEach(order -> System.out.println(order.toString()));
		return readyToServeList;
	}

	@POST
	public void markOrderComplete() {
		System.out.println( " Marking Orders as Completed... ");
		readyToServeList.forEach(order -> order.setStatus(Status.COMPLETED));
	}


}
