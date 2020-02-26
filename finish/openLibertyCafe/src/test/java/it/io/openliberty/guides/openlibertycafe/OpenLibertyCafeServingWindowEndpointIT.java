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

import io.openliberty.guides.openlibertycafe.OpenLibertyCafeServingWindowResource;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;

import javax.ws.rs.core.Response;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class OpenLibertyCafeServingWindowEndpointIT {

    @RESTClient
    public static OpenLibertyCafeServingWindowResource servingWindowResource;

    @Test
    @Order(1)
    public void testGetReady2Serve() {
        Response response = servingWindowResource.getReady2Serve();
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));
    }

    @Test
    @Order(2)
    public void testGetSingleOrder() {
        Response response = servingWindowResource.serveOrder("0001");
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));
    }

}
