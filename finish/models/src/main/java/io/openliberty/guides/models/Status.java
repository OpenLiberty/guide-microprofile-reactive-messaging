package io.openliberty.guides.models;

public enum Status {
    NEW,            // The order has just been sent
    IN_PROGRESS,    // The order has reached the food/beverage service via Kafka
    READY,          // The order is ready to be picked up by the foodServing service
    COMPLETED;      // The order has been picked up, this is the final status.
}