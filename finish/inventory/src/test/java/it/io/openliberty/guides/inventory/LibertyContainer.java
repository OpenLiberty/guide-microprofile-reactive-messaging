// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.inventory;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class LibertyContainer extends GenericContainer<LibertyContainer> {

    static final Logger LOGGER = LoggerFactory.getLogger(LibertyContainer.class);

    private String baseURL;

    public LibertyContainer(final String dockerImageName) {
        super(dockerImageName);
        // wait for the smarter planet message by default
        waitingFor(Wait.forLogMessage("^.*CWWKF0011I.*$", 1));
        addExposedPorts(9085);
    }

    public <T> T createRestClient(Class<T> clazz, String applicationPath) {
        String urlPath = getBaseURL();
        if (applicationPath != null) {
            urlPath += applicationPath;
        }
        JAXRSClientFactoryBean client = new JAXRSClientFactoryBean();
        client.setResourceClass(clazz);
        client.setAddress(urlPath);
        return client.create(clazz);
    }

    public String getBaseURL() throws IllegalStateException {
        if (baseURL != null) {
            return baseURL;
        }
        if (!this.isRunning()) {
            throw new IllegalStateException(
                "Container must be running to determine hostname and port");
        }
        baseURL = "http://" + this.getHost() + ":" + this.getFirstMappedPort();
        return baseURL;
    }
}
