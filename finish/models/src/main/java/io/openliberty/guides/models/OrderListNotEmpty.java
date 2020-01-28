package io.openliberty.guides.models;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OrderListValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderListNotEmpty {

    String message() default "Order request must contain at least one food or drink item!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}