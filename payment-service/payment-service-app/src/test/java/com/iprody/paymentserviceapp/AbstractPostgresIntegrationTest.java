package com.iprody.paymentserviceapp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest
@Testcontainers
public abstract class AbstractPostgresIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES = new
        PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("payment-db")
        .withUsername("test")
        .withPassword("test")
        .waitingFor(new WaitAllStrategy()
            .withStrategy(Wait.forListeningPort())
            .withStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*", 2)))
                .withStartupTimeout(Duration.ofSeconds(200));

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(
        DockerImageName.parse("apache/kafka:4.0.0")
    );

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/master-test-changelog.yaml");
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.listener.auto-startup", () -> "false");
    }
}
