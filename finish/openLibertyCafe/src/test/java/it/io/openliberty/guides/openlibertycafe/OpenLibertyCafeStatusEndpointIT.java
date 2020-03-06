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

import io.openliberty.guides.openlibertycafe.OpenLibertyCafeStatusResource;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class OpenLibertyCafeStatusEndpointIT {

    @RESTClient
    public static OpenLibertyCafeStatusResource statusResource;
    
    @Test
    @Order(1)
    public void testGetOrders() {
        Response response = (Response) statusResource.getOrders();
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));
    }

    @Test
    @Order(2)
    public void testGetSingleOrder() {
        Response response = (Response) statusResource.getSingleOrder("0001");
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));
    }

    @Test
    @Order(3)
    public void testGetOrdersList() {
        Response response = (Response) statusResource.getOrdersList("0001");
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));
    }
}
