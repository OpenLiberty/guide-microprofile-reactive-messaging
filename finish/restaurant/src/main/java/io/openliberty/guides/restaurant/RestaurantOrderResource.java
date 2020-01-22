package io.openliberty.guides.restaurant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.restaurant.client.OrderClient;

@ApplicationScoped
@Path("/orders")
public class RestaurantOrderResource {

    @Inject
    private OrderClient orderClient;

    OrderList newOrderList = null;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOrders(){ //TODO Return list of all orders, still have to figure out how to store orders
        return orderClient.getOrders();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(NewOrder order){
        //Consumes an Order from the end user in a specific format. See finish/order.json as an example
        newOrderList = new OrderList(order); // Divides the new order into multiple single orders

        //Submits each food/drink order individually to the OrderClient class to send to the Order service
        for(Order aSingleOrder : newOrderList.getOrderArrayList()){
            orderClient.createOrder(aSingleOrder);
        }

        return Response
                .status(Response.Status.OK)
                .entity(newOrderList)
                .build();
    }
}