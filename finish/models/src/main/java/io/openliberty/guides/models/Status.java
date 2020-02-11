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

public enum Status {
    NEW,            // The order has just been sent
    IN_PROGRESS,    // The order has reached the kitchen/bar service via Kafka
    READY,          // The order is ready to be picked up by the servingWindow service
    COMPLETED;      // The order has been picked up, this is the final status.
}