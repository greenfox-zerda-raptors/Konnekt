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
    @Profile(Profiles.DEV)
    public DataSource getDevDataSource() throws URISyntaxException {
        return createPostgresDataSource("dev");
    }

    @Bean(name = "securityDataSource")
    @Profile(Profiles.PROD)
    public DataSource getProdDataSource() throws URISyntaxException {
        return createPostgresDataSource("prod");
    }

    @Bean(initMethod = "migrate")
    Flyway flyway() throws URISyntaxException {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setSchemas("konnekt");
        flyway.setLocations("filesystem:src/main/java/com/greenfoxacademy/db/migration");
        flyway.setDataSource((DataSource) appContext.getBean("securityDataSource"));
        return flyway;
    }

    private DriverManagerDataSource createPostgresDataSource(String profile) throws URISyntaxException{
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        if (profile.equals("prod")) {
            String password = dbUri.getUserInfo().split(":")[1];
            dbUrl += "?sslmode=require";
            dataSource.setPassword(password);
        }
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        return dataSource;
    }
}
