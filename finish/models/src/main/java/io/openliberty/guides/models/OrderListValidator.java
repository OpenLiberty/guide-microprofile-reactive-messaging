package io.openliberty.guides.models;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OrderListValidator implements ConstraintValidator<OrderListNotEmpty, OrderRequest> {

    @Override
    public boolean isValid(OrderRequest orderRequest, ConstraintValidatorContext constraintValidatorContext) {
        int foodListSize = orderRequest.getFoodList().size();
        int beverageListSize = orderRequest.getBeverageList().size();

        return (foodListSize != 0 || beverageListSize != 0);
    }
}
