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

public class JobListValidator implements ConstraintValidator<JobListNotEmpty, JobRequest> {

    @Override
    public boolean isValid(JobRequest jobRequest, ConstraintValidatorContext constraintValidatorContext) {
        int taskListSize = jobRequest.getTaskList().size();
        return (taskListSize != 0);
    }
}
