package com.greenfoxacademy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by posam on 2017-01-23.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonPropertiesConfiguration extends PropertiesConfiguration {

    @Bean
    @Profile(Profiles.DEV)
    public static PropertySourcesPlaceholderConfigurer devProperties() {
        return createPropertySourcesPlaceholderConfigurer(
                "application_dev.properties");
    }
    @Bean
    @Profile(Profiles.DEV)
    public static PropertySourcesPlaceholderConfigurer prodProperties() {
        return createPropertySourcesPlaceholderConfigurer(
                "application_prod.properties");
    }

    @Bean
    @Profile(Profiles.DEVMYSQL)
    public static PropertySourcesPlaceholderConfigurer mysqlProperties() {
        return createPropertySourcesPlaceholderConfigurer(
                "application_devmysql.properties");
    }
}