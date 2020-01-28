package io.openliberty.guides.models;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OrderListValidator implements ConstraintValidator<OrderListNotEmpty, OrderRequest> {

    @Override
    public boolean isValid(OrderRequest orderRequest, ConstraintValidatorContext constraintValidatorContext) {
        int foodListSize = orderRequest.getFoodList().size();
        int drinkListSize = orderRequest.getDrinkList().size();

        return (foodListSize != 0 || drinkListSize != 0);
    }
}
