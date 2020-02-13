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
package it.io.openliberty.guides.kitchen;

import org.microshed.testing.ApplicationEnvironment;
import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.microshed.testing.testcontainers.config.HollowTestcontainersConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;

public class AppContainerConfig implements SharedContainerConfiguration {

    private static Network network = Network.newNetwork();

    @Container
    public static KafkaContainer kafka = new KafkaContainer()
        .withNetwork(network);
    
    @Container
    public static ApplicationContainer app = new ApplicationContainer()
                    .withAppContextRoot("/")
                    .withExposedPorts(new Integer(9083))
                    .withReadinessPath("/kitchen/foodMessaging")
                    .withNetwork(network);
    
    @Override
    public void startContainers() {
        if (ApplicationEnvironment.Resolver.load().getClass() == HollowTestcontainersConfiguration.class) {
            // Run in dev mode. 
            // The application talks to KafkaContainer from outside of the Docker network,
            // and it can talk to kafka directly on 9093. 
            // The MicroProfile configure should define as following:
            // mp.messaging.connector.liberty-kafka.bootstrap.servers=localhost:9093
        } else {
            // Run by maven verify goal.
            // The application talks to KafkaContainer within Docker network, 
            // and it need to talk to the broker on port 9092
            kafka.withNetworkAliases("kafka");
            app.withEnv("MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092");
        }
        kafka.start();
        app.start();
    }
    
}