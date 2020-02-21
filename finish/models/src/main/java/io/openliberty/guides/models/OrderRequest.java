// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.models;

import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@OrderListNotEmpty
public class OrderRequest {

    @NotNull(message="Table ID is not specified!")
    @Pattern(regexp="^\\d+$", message="Table ID must be a non-negative number!")
    private String tableId;

    private ArrayList<@NotBlank(message="Food item name cannot be an empty string!") String> foodList = new ArrayList<>();

    private ArrayList<@NotBlank(message="Beverage item name cannot be an empty string!") String> beverageList = new ArrayList<>();

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public ArrayList<String> getFoodList() {
        return foodList;
    }

    public void setFoodList(ArrayList<String> foodList) {
        this.foodList = foodList;
    }

    public ArrayList<String> getBeverageList() {
        return beverageList;
    }

    public void setBeverageList(ArrayList<String> beverageList) {
        this.beverageList = beverageList;
    }
}
