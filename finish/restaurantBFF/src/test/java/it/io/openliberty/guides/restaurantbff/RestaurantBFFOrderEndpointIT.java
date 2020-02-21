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
package it.io.openliberty.guides.restaurantbff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.openliberty.guides.restaurantbff.RestaurantBFFOrderResource;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;

import javax.ws.rs.core.Response;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class RestaurantBFFOrderEndpointIT {

    @RESTClient
    public static RestaurantBFFOrderResource orderResource;

    @Test
    @Order(1)
    public void testGetOrders() {
        Response response = orderResource.getOrders();
        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Content-Type"), "application/json");
    }

    @Test
    @Order(2)
    public void testGetSingleOrder() {
        Response response = orderResource.getSingleOrder("0001");
        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Content-Type"), "application/json");
    }

    @Test
    @Order(3)
    public void testCreateOrder() {
        Response response = orderResource.createOrder(AppContainerConfig.orderRequest);
        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Content-Type"), "application/json");
    }

}
