package io.openliberty.guides.restaurant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.OrderList;
import io.openliberty.guides.restaurant.client.OrderClient;

@ApplicationScoped
@Path("/orders")
public class RestaurantOrderResource {

    @Inject
    private OrderClient orderClient;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public OrderList getOrders(){ //TODO Return list of all orders
        return null;
    }

    @GET
    @Path("orderDrink")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderDrink(){ //TODO Change from Response return to Order object
        return orderClient.createDrink();
    }

    @GET
    @Path("orderFood")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderFood(){ //TODO Submit an order for a drink + return new drink object
        return orderClient.createFood();
    }
}