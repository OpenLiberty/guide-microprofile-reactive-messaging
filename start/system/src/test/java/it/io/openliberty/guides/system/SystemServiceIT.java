// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020, 2023 IBM Corporation and others.
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

import static org.junit.Assert.assertNotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.openliberty.guides.models.SystemLoad;
import io.openliberty.guides.models.SystemLoad.SystemLoadDeserializer;

@Testcontainers
public class SystemServiceIT {

    private static final String APP_IMAGE = "system:1.0-SNAPSHOT";
    private static final String KAFKA_IMAGE = "confluentinc/cp-kafka:5.4.3";

    private static Logger logger = LoggerFactory.getLogger(SystemServiceIT.class);
    private static Network network = Network.newNetwork();

    @Container
    private static KafkaContainer kafka =
        new KafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
            .withNetworkAliases("kafka")
            .withNetwork(network);    

    @Container
    private static LibertyContainer libertyContainer =
        new LibertyContainer(APP_IMAGE)
            .withEnv("MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS",
                "kafka:9092")
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withStartupTimeout(Duration.ofMinutes(3))
            .dependsOn(kafka)
            .waitingFor(Wait.forHttp("/health/ready"));

    private static KafkaConsumer<String, SystemLoad> consumer;

    @BeforeAll
    private static void setup() {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "system-load-status");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(props,
                       new StringDeserializer(),
                       new SystemLoadDeserializer());
        consumer.subscribe(Arrays.asList("system.load"));
    }

    @Test
    public void testCpuStatus() {
        ConsumerRecords<String, SystemLoad> records =
                consumer.poll(Duration.ofMillis(30 * 1000));
        System.out.println("Polled " + records.count() + " records from Kafka:");
        Assertions.assertFalse(records.isEmpty(), "No record from Kafka");
        for (ConsumerRecord<String, SystemLoad> record : records) {
            SystemLoad sl = record.value();
            System.out.println("    " + sl);
            assertNotNull(sl.hostname);
            assertNotNull(sl.loadAverage);
        }
        consumer.commitAsync();
    }
}
