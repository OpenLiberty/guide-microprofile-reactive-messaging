package io.openliberty.guides.restaurant;

import io.openliberty.guides.restaurant.client.ServerClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/server")
public class RestaurantServerResource {

    @Inject
    private ServerClient serverClient;

    //Returns list of all ready orders
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReady2Serve(){
        return serverClient.getReady2Serve();
    }

    @POST
    @Path("complete/{orderID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serveOrder(@PathParam("orderID") String orderID){
        return serverClient.serveOrder(orderID);
    }
}