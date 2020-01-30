package io.openliberty.guides.restaurantbff;

import io.openliberty.guides.restaurantbff.client.ServingWindowClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/servingWindow")
public class RestaurantBFFServingWindowResource {

    @Inject
    private ServingWindowClient servingWindowClient;

    //Returns list of all ready orders
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReady2Serve(){
        return servingWindowClient.getReady2Serve();
    }

    @POST
    @Path("complete/{orderID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response serveOrder(@PathParam("orderID") String orderID){
        return servingWindowClient.serveOrder(orderID);
    }
}