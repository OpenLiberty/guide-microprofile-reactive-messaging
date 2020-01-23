package io.openliberty.guides.restaurant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.NewOrder;
import io.openliberty.guides.restaurant.client.OrderClient;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.ArrayList;

@ApplicationScoped
@Path("/orders")
public class RestaurantOrderResource {

    @Inject
    private OrderClient orderClient;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOrders(){ //TODO Return list of all orders, still have to figure out how to store orders
        return orderClient.getOrders();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(NewOrder newOrder){
        return orderClient.createOrder(newOrder);
    }
}