package io.openliberty.guides.models;

public class Order {
    private int orderID;
    private int tableID;
    private Type type;
    private String item;
    private Status status;

    public Order(int tableID, String item, Type type){
        this.tableID = tableID;
        this.item = item;
        this.type = type;
    }

    public int getTableID() {
        return tableID;
    }

    public Order setTableID(int tableID) {
        this.tableID = tableID;
        return this;
    }

    public String getItem() {
        return item;
    }

    public Order setItem(String item) {
        this.item = item;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Order setType(Type type) {
        this.type = type;
        return this;
    }

    public int getOrderID() {
        return orderID;
    }

    public Order setOrderID(int orderID) {
        this.orderID = orderID;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Order setStatus(Status status) {
        this.status = status;
        return this;
    }
}