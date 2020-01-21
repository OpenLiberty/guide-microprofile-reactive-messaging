package io.openliberty.guides.restaurant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.OrderList;
import io.openliberty.guides.models.newOrder;
import io.openliberty.guides.restaurant.client.OrderClient;

@ApplicationScoped
@Path("/orders")
public class RestaurantOrderResource {

    @Inject
    private OrderClient orderClient;

    OrderList newOrderList = null;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOrders(){ //TODO Return list of all orders, needs to consume
        if(newOrderList == null){
            return null;
        }else{
            return Response
                    .status(Response.Status.OK)
                    .entity(newOrderList)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrder(newOrder order){
        newOrderList = new OrderList(order); // Divides the new order into multiple single orders

        //Submits each food/drink order to the OrderClient class to send to the Order service
        for(Order aSingleFoodOrder : newOrderList.getFoodOrderArrayList()){
            orderClient.createFood(aSingleFoodOrder);
        }

        for(Order aSingleDrinkOrder : newOrderList.getDrinkOrderArrayList()){
            orderClient.createDrink(aSingleDrinkOrder);
        }

        return Response
                .status(Response.Status.OK)
                .entity(newOrderList)
                .build();
    }
}