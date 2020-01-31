// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.kitchen;

import org.microshed.testing.ApplicationEnvironment;
import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.MicroProfileApplication;
import org.microshed.testing.testcontainers.config.HollowTestcontainersConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;

public class AppContainerConfig implements SharedContainerConfiguration {

	private static Network network = Network.newNetwork();
	
    @Container
    public static KafkaContainer kafka = new KafkaContainer()
        .withNetworkAliases("kafka")
        .withNetwork(network);
    
    @Container
    public static MicroProfileApplication app = new MicroProfileApplication()
                    .withAppContextRoot("/")
                    .withReadinessPath("/system/properties")
                    .withNetwork(network);
    
    @Override
    public void startContainers() {
        // If talking to KafkaContainer from within Docker network we need to talk to the broker on port 9092
        // If talking to KafkaContainer from outside of the Docker network we can talk to kafka directly on 9093
        if (ApplicationEnvironment.load().getClass() == HollowTestcontainersConfiguration.class) {
            app.withEnv("KAFKA_SERVER", "localhost:9093");
        } else {
            app.withEnv("KAFKA_SERVER", "kafka:9092");
        }

        kafka.start();
    	app.start();
    }
    
}
