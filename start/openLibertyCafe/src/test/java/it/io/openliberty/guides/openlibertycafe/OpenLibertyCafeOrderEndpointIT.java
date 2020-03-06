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
package it.io.openliberty.guides.openlibertycafe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;

import io.openliberty.guides.models.OrderRequest;
import io.openliberty.guides.openlibertycafe.OpenLibertyCafeOrderResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class OpenLibertyCafeOrderEndpointIT {

    @RESTClient
    public static OpenLibertyCafeOrderResource orderResource;

    @Test
    @Order(1)
    public void testCreateOrder() {
        Response response = orderResource.createOrder(AppContainerConfig.orderRequest);
        assertEquals(200, response.getStatus());
    }
    
    @Test
    @Order(2)
    public void testCreateInvalidOrder() {
        Response response = orderResource.createOrder(new OrderRequest());
        assertEquals(400, response.getStatus());
    }

}
