package io.openliberty.guides.models;

import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class OrderRequest {

    @NotNull(message="Table ID is not specified!")
    @Pattern(regexp="^\\d+$", message="Table ID must be a non-negative number!")
    private String tableID;

    private ArrayList<@NotBlank(message="Food item name cannot be an empty string!") String> foodList = new ArrayList<>();

    private ArrayList<@NotBlank(message="Drink item name cannot be an empty string!") String> drinkList = new ArrayList<>();

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
