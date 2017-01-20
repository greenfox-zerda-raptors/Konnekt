package com.greenfoxacademy.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class KonnektAppConfig {

    @Bean(name = "securityDataSource")
    public DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource =
                new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/konnekt");
        // Configure user name
        driverManagerDataSource.setUsername("root");
        // Obtain password from environmental variable
        driverManagerDataSource.setPassword(System.getenv("DB_PASSWORD"));
        return driverManagerDataSource;
    }

//    @Bean(name = "securityDataSource")
//    public DriverManagerDataSource dataSource() throws URISyntaxException {
//        URI dbUri = new URI(System.getenv("DATABASE_URL"));
//
//        String username = dbUri.getUserInfo().split(":")[0];
//        String password = dbUri.getUserInfo().split(":")[1];
//        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
//
//        DriverManagerDataSource basicDataSource = new DriverManagerDataSource();
//        basicDataSource.setUrl(dbUrl);
//        basicDataSource.setUsername(username);
//        basicDataSource.setPassword(password);
//
//        return basicDataSource;
//    }


    @Bean(initMethod = "migrate")
    Flyway flyway() throws URISyntaxException {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setSchemas("konnekt");
        flyway.setLocations("filesystem:src/main/java/com/greenfoxacademy/db/migration");
        flyway.setDataSource(dataSource());
        return flyway;
    }
}
