package com.greenfoxacademy.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class KonnektAppConfig {

    private final ApplicationContext appContext;

    @Autowired
    public KonnektAppConfig(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Bean(name = "securityDataSource")
    @Profile({Profiles.DEV})
    public DataSource getDevDataSource() throws URISyntaxException {
        return createPostgresDataSource("dev");
    }

    @Bean(name = "securityDataSource")
    @Profile(Profiles.PROD)
    public DataSource getProdDataSource() throws URISyntaxException {
        return createPostgresDataSource("prod");
    }

    @Bean(name = "securityDataSource")
    @Profile(Profiles.TEST)
    public DataSource getTestDataSource() throws URISyntaxException {
        return createPostgresDataSource("test");
    }

    @Bean(initMethod = "migrate")
    @Profile({Profiles.PROD, Profiles.DEV})
    Flyway flyway() throws URISyntaxException {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setSchemas("konnekt");
        flyway.setLocations("filesystem:src/main/java/com/greenfoxacademy/db/migration");
        flyway.setDataSource((DataSource) appContext.getBean("securityDataSource"));
        flyway.repair();
        return flyway;
    }

    @Bean(initMethod = "migrate", name = "flyway")
    @Profile(Profiles.TEST)
    Flyway flywayTest() throws URISyntaxException {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setSchemas("konnekt_test");
        flyway.setLocations("filesystem:src/main/java/com/greenfoxacademy/db/migration");
        flyway.setDataSource((DataSource) appContext.getBean("securityDataSource"));
        flyway.repair();
        return flyway;
    }

    private DriverManagerDataSource createPostgresDataSource(String profile) throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String[] userPass = dbUri.getUserInfo().split(":");
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername(userPass[0]);
        dataSource.setDriverClassName("org.postgresql.Driver");
        switch (profile) {
            case "dev":
                dataSource.setSchema("konnekt");
                break;
            case "prod":
                String password = userPass[1];
                dbUrl += "?sslmode=require";
                dataSource.setPassword(password);
                dataSource.setSchema("konnekt");
                break;
            case "test":
                if (userPass.length == 2) {
                    String pw = userPass[1];
                    dataSource.setPassword(pw);
                }
                dataSource.setSchema("konnekt_test");
                dbUrl += (dbUri.getHost().contains("amazon")) ? "?sslmode=require" : "";


        }
        dataSource.setUrl(dbUrl);
        return dataSource;
    }


}