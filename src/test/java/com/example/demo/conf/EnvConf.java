package com.example.demo.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Provides a real PostgreSQL instance (via Testcontainers) for integration tests, and wires the
 * Spring datasource properties to it. Instantiated reflectively by {@link FacadeIT}.
 */
public class EnvConf {

  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withDatabaseName("cinema")
          .withUsername("cinema")
          .withPassword("cinema");

  static {
    POSTGRES.start();
  }

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }
}
