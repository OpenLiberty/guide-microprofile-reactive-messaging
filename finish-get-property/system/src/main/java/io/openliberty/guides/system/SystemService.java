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
package io.openliberty.guides.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import io.openliberty.guides.models.SystemLoad;
import io.openliberty.guides.models.MemoryStatus;
import io.openliberty.guides.models.PropertyMessage;
import io.reactivex.rxjava3.core.Flowable;

@ApplicationScoped
public class SystemService {
	
	private static Logger logger = Logger.getLogger(SystemService.class.getName());
    
    private static final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
    private static final OperatingSystemMXBean osMean = ManagementFactory.getOperatingSystemMXBean();
    private static String hostname = null;
    
    private static String getHostname() {
        if (hostname == null) {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                return System.getenv("HOSTNAME");
            }
        }
        return hostname;
    }
    
    // tag::publishSystemLoad[]
    @Outgoing("systemLoad")
    // end::publishSystemLoad[]
    public Publisher<SystemLoad> sendSystemLoad() {
        return Flowable.interval(15, TimeUnit.SECONDS)
                .map((interavl -> new SystemLoad(getHostname(), new Double(osMean.getSystemLoadAverage()))));
    }

    // tag::publishMemoryUsage[]
    @Outgoing("memoryStatus")
    // end::publishMemoryUsage[]
    public Publisher<MemoryStatus> sendMemoryUsage() {
        return Flowable.interval(15, TimeUnit.SECONDS)
                .map((interavl -> {
                    return new MemoryStatus(getHostname() , 
                            new Long(memBean.getHeapMemoryUsage().getUsed()), 
                            new Long(memBean.getHeapMemoryUsage().getMax()));}));
    }

    @Incoming("getProperty")
    @Outgoing("setProperty")
    public Publisher<PropertyMessage> sendProperty(String property) {
    	logger.info("sendProperty: " + property);
        return Flowable.interval(15, TimeUnit.SECONDS)
                .map((interavl -> {
                    return new PropertyMessage(getHostname(), 
                    		property, 
                            System.getProperty(property, "unknown"));}));
    }
    
}