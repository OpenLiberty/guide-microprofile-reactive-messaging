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
package io.openliberty.guides.restaurantbff.client;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
public class ServingWindowClient {

    @Inject
    @ConfigProperty(name = "SERVINGWINDOW_SERVICE_HOSTNAME", defaultValue = "localhost")
    private String hostname;

    @Inject
    @ConfigProperty(name = "SERVINGWINDOW_SERVICE_PORT", defaultValue = "9082")
    private String port;

    private WebTarget target;
    private String baseUri;

    public ServingWindowClient() {
        this.target = null;
    }

    public Response getReady2Serve(){
        return iBuilder(webTarget())
                .get();
    }

    public Response serveOrder(String orderID){
        return iBuilder(webTarget().path("complete/" + orderID))
                .post(null);
    }

    private Invocation.Builder iBuilder(WebTarget target) {
        return target
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    //Sets target to endpoint provided by Order API
    private WebTarget webTarget() {
        if (this.target == null) {
            baseUri = "http://" + hostname + ":" + port;

            this.target = ClientBuilder
                    .newClient()
                    .target(baseUri)
                    .path("/servingWindow");
        }
        return this.target;
    }
}