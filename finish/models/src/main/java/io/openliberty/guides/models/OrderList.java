package io.openliberty.guides.models;

import java.util.ArrayList;

public class OrderList{
    private ArrayList<Order> foodOrderArrayList = new ArrayList<>();
    private ArrayList<Order> drinkOrderArrayList = new ArrayList<>();
    private int tableID;

    public OrderList(newOrder order){ // Creates a list of Orders to easily feed into the Order microservice
        tableID = order.getTableID();

        Order newFoodOrder = new Order();
        newFoodOrder.setTableID(tableID);

        //Parses through the newOrder object and creates single orders of FOOD types
        newFoodOrder.setType(Type.FOOD);
        for(String foodItem : order.getFoodList()){
            newFoodOrder.setItem(foodItem);
            foodOrderArrayList.add(newFoodOrder);
        }

        newFoodOrder.setType(Type.DRINK);
        for(String drinkItem : order.getDrinkList()){
            newFoodOrder.setItem(drinkItem);
            drinkOrderArrayList.add(newFoodOrder);
        }
    }

    public ArrayList<Order> getFoodOrderArrayList(){
        return foodOrderArrayList;
    }

    public void setFoodOrderArrayList(ArrayList<Order> foodOrderArrayList) {
        this.foodOrderArrayList = foodOrderArrayList;
    }

    public ArrayList<Order> getDrinkOrderArrayList(){
        return drinkOrderArrayList;
    }

    public void setDrinkOrderArrayList(ArrayList<Order> drinkOrderArrayList) {
        this.drinkOrderArrayList = drinkOrderArrayList;
    }

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }
}