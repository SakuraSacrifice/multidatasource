package com.lee.multidatasource.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class ExcludeInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String EXCLUDE_PROPERTY_SOURCE_NAME = "EXCLUDE_PROPERTY_SOURCE_NAME";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        Properties properties = new Properties();
        properties.setProperty("spring.autoconfigure.exclude",
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration");

        environment.getPropertySources().addLast(new PropertiesPropertySource(
                EXCLUDE_PROPERTY_SOURCE_NAME, properties));
    }

}