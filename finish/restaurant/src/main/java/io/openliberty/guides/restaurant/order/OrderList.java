package io.openliberty.guides.restaurant.order;

import io.openliberty.guides.models.Order;
import io.openliberty.guides.models.Type;

import java.util.ArrayList;

public class OrderList{
    private ArrayList<Order> orderArrayList = new ArrayList<>();

    public OrderList(newOrder order){ // Creates a list of Orders to easily feed into the Order microservice
        String tableID = order.getTableID();

        //Parses through the newOrder object and creates single orders of FOOD types
        for(String foodItem : order.getFoodList()){
            Order newFoodOrder = new Order(tableID, foodItem, Type.FOOD);
            orderArrayList.add(newFoodOrder);
        }

        for(String drinkItem : order.getDrinkList()){
            Order newFoodOrder = new Order(tableID, drinkItem, Type.DRINK);
            orderArrayList.add(newFoodOrder);
        }
    }

    public ArrayList<Order> getOrderArrayList(){
        return orderArrayList;
    }

    public void setFoodOrderArrayList(ArrayList<Order> orderArrayList) {
        this.orderArrayList = orderArrayList;
    }
}