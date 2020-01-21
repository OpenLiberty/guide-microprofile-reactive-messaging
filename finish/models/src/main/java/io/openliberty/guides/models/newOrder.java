package io.openliberty.guides.models;

import java.util.ArrayList;

public class newOrder {
    private String tableID;
    private ArrayList<String> foodList = new ArrayList<>();
    private ArrayList<String> drinkList = new ArrayList<>();

    public String getTableID() {
        return tableID;
    }

    public void setTableID(String tableID) {
        this.tableID = tableID;
    }

    public ArrayList<String> getFoodList() {
        return foodList;
    }

    public void setFoodList(ArrayList<String> foodList) {
        this.foodList = foodList;
    }

    public ArrayList<String> getDrinkList() {
        return drinkList;
    }

    public void setDrinkList(ArrayList<String> drinkList) {
        this.drinkList = drinkList;
    }
}
