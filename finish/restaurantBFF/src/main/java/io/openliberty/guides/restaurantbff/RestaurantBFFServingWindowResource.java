package io.openliberty.guides.restaurantbff;

import io.openliberty.guides.restaurantbff.client.ServingWindowClient;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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
    @Tag(name = "Serving Window",
            description = "Listing and completing 'READY' Orders")
    public Response getReady2Serve(){
        return servingWindowClient.getReady2Serve();
    }

    //Completes a ready order of a particular orderId
    @POST
    @Path("complete/{orderID}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Serving Window")
    public Response serveOrder(@PathParam("orderID") String orderID){
        return servingWindowClient.serveOrder(orderID);
    }
}