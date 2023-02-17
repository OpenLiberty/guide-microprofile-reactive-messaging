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
package it.io.openliberty.guides.inventory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
public class InventoryServiceIT {

    private static final Jsonb JSONB = JsonbBuilder.create();
    private static final String APP_IMAGE = "inventory:1.0-SNAPSHOT";
    private static final String KAFKA_IMAGE = "confluentinc/cp-kafka:5.4.3";

    private static Logger logger = LoggerFactory.getLogger(InventoryServiceIT.class);
    private static Network network = Network.newNetwork();

    private static InventoryResourceClient client;

    @Container
    private static KafkaContainer kafka =
        new KafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
            .withNetworkAliases("kafka")
            .withNetwork(network);

    @Container
    private static LibertyContainer libertyContainer = new LibertyContainer(APP_IMAGE)
          .withEnv("MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withStartupTimeout(Duration.ofMinutes(3))
            .dependsOn(kafka)
            .waitingFor(Wait.forHttp("/health/ready"));

    public static KafkaProducer<String,SystemLoad> producer;

    @BeforeAll
    private static void setup() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers());
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            "io.openliberty.guides.models.SystemLoad$SystemLoadSerializer");
        props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producer = new KafkaProducer<String,SystemLoad>(props);
        client = libertyContainer.createRestClient(InventoryResourceClient.class, "/");
    }

    @AfterAll
    public static void cleanup() {
        client.resetSystems();
    }

    @Test
    public void testCpuUsage() throws InterruptedException {
        SystemLoad sl = new SystemLoad("localhost", 1.1);
        ProducerRecord<String, SystemLoad> record =
            new ProducerRecord<String, SystemLoad>("system.load", sl);
        producer.send(record);
        Thread.sleep(5000*2);
        Response response = client.getSystems();
        Assertions.assertEquals(200, response.getStatus(),
            "Response should be 200");
        List<Map> systems =
            JSONB.fromJson(response.readEntity(String.class), List.class);
        Assertions.assertEquals(systems.size(), 1);
           Map system = systems.get(0);
        Assertions.assertEquals(sl.hostname, system.get("hostname"),
            "Hostname doesn't match!");
        BigDecimal systemLoad = (BigDecimal) system.get("systemLoad");
        Assertions.assertEquals(sl.loadAverage, systemLoad.doubleValue(),
            "CPU load doesn't match!");
    }
}
