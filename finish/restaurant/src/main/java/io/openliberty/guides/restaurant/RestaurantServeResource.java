package io.openliberty.guides.restaurant;

import io.openliberty.guides.restaurant.client.ServeClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/serve")
public class RestaurantServeResource {

    @Inject
    private ServeClient serveClient;

    //Returns all orders that are ready to serve
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReady2Serve(){
        return serveClient.getReady2Serve();
    }

    @GET
    @Path(":{orderID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serveOrder(@PathParam("orderID") String orderID){
        return serveClient.serveOrder(orderID);
    }
}