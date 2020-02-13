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
