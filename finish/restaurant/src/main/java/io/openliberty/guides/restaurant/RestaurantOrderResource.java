package io.openliberty.guides.restaurant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.OrderRequest;
import io.openliberty.guides.restaurant.client.OrderClient;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/orders")
public class RestaurantOrderResource {

    @Inject
    private OrderClient orderClient;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(operationId = "listAllOrders",
            summary = "Lists all of the submitted orders",
            description = "This operation retrieves all of the submitted orders " +
                    "and order details from the order database")
    @Tag(name = "Order",
            description = "Submitting and listing Orders")
    public Response listOrders(){ //TODO Return list of all orders, still have to figure out how to store orders
        return orderClient.getOrders();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Order")
    public Response createOrder(OrderRequest orderRequest){
        return orderClient.createOrder(orderRequest);
    }
}