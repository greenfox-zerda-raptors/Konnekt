package com.greenfoxacademy.config;

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
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/");
        // Configure user name and password for database connection
        driverManagerDataSource.setUsername("root");
        driverManagerDataSource.setPassword("admin1234");
        return driverManagerDataSource;
    }
}
