// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020, 2021 IBM Corporation and others.
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
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
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

	private static Logger logger = LoggerFactory.getLogger(SystemServiceIT.class);
	private static String appImageName = "liberty-deepdive-inventory:1.0-SNAPSHOT";
	
	public static Network network = Network.newNetwork();
	private static String kafkaImageName = "bitnami/kafka:2";
	
    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse(kafkaImageName))
        .withNetwork(network);
    
	
    //@KafkaConsumerClient(valueDeserializer = SystemLoadDeserializer.class,
    //        groupId = "system-load-status",
    //       topics = "system.load",
    //        properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    
        
    public static KafkaConsumer<String, SystemLoad> consumer =
        new KafkaConsumer<>(getKafkaConsumerProperties(),
            new StringDeserializer(),
            new SystemLoadDeserializer());
    
    @Container
    public static LibertyContainer libertyContainer
        = new LibertyContainer(appImageName)
              .withNetwork(network)
              .waitingFor(Wait.forHttp("/health/ready"))
              .withLogConsumer(new Slf4jLogConsumer(logger));

    private static Properties getKafkaConsumerProperties() {
    	Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "system-load-status");
        props.setProperty("topics", "system.load");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return props;
    }
    
    @Test
    public void testCpuStatus() {
        ConsumerRecords<String, SystemLoad> records =
                consumer.poll(Duration.ofMillis(30 * 1000));
        System.out.println("Polled " + records.count() + " records from Kafka:");

        for (ConsumerRecord<String, SystemLoad> record : records) {
            SystemLoad sl = record.value();
            System.out.println(sl);
            assertNotNull(sl.hostname);
            assertNotNull(sl.loadAverage);
        }
        consumer.commitAsync();
    }
}
