// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;

// imports for a JAXRS client to simplify the code

// logger imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// testcontainers imports
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
// simple import to build a URI/URL
import javax.ws.rs.core.UriBuilder;



public class LibertyContainer extends GenericContainer<LibertyContainer> {

    static final Logger LOGGER = LoggerFactory.getLogger(LibertyContainer.class);

    private String baseURL;

    private KeyStore keystore;
    private SSLContext sslContext;

    public static String getProtocol() {
        return System.getProperty("test.protocol", "http");
    }

    public static boolean testHttps() {
        return getProtocol().equalsIgnoreCase("https");
    }

    public LibertyContainer(final String dockerImageName) {
        super(dockerImageName);
        // wait for smarter planet message by default
        waitingFor(Wait.forLogMessage("^.*CWWKF0011I.*$", 1));
        init();
    }

    // tag::createRestClient[]
    public <T> T createRestClient(Class<T> clazz, String applicationPath) {
        String urlPath = getBaseURL();
        if (applicationPath != null) {
            urlPath += applicationPath;
        }
        ClientBuilder builder = ClientBuilder.newBuilder();
        if (testHttps()) {
            builder.sslContext(sslContext);
            builder.trustStore(keystore);
        }
        Client client = builder.build();
        WebTarget target = client.target(UriBuilder.fromPath(urlPath));
        //return target.proxy(clazz);
        
        
        JAXRSClientFactoryBean bean = new org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean();
        bean.setResourceClass(clazz);
        bean.setAddress(applicationPath);
        return bean.create(clazz);
    }
    // end::createRestClient[]

    // tag::getBaseURL[]
    public String getBaseURL() throws IllegalStateException {
        if (baseURL != null) {
            return baseURL;
        }
        if (!this.isRunning()) {
            throw new IllegalStateException(
                "Container must be running to determine hostname and port");
        }
        baseURL =  getProtocol() + "://" + this.getContainerIpAddress()
            + ":" + this.getFirstMappedPort();
        System.out.println("TEST: " + baseURL);
        return baseURL;
    }
    // end::getBaseURL[]

    private void init() {
        this.addExposedPorts(9080);
    }
}
