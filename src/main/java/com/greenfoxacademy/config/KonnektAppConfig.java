package com.greenfoxacademy.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class KonnektAppConfig {

    @Bean(name = "securityDataSource")
    public DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource =
                new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.postgresql.jdbc3.Jdbc3ConnectionPool");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/");
        // Configure user name
        driverManagerDataSource.setUsername("postgres");
        // Obtain password from environmental variable
        driverManagerDataSource.setPassword(System.getenv("DB_PASSWORD"));
        return driverManagerDataSource;
    }

    @Bean(initMethod = "migrate")
    Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setSchemas("konnekt");
        flyway.setLocations("filesystem:/src/main/java/com/greenfoxacademy/db/migration");
        flyway.setDataSource(dataSource());
        return flyway;
    }

}
