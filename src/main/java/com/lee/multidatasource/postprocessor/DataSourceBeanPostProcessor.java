package com.lee.multidatasource.postprocessor;

import com.alibaba.druid.pool.DruidDataSource;
import com.lee.multidatasource.config.LeeMultiPersistenceConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static com.lee.multidatasource.constant.CommonConstants.KEY_URL;

public class DataSourceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariDataSource) {
            assembleHikariDataSource((HikariDataSource) bean, beanName);
        } else if (bean instanceof DruidDataSource) {
            assembleDruidDatasource((DruidDataSource) bean, beanName);
        } else if (bean instanceof DataSource) {
            assembleTomcatJdbcDatasource((DataSource) bean, beanName);
        }
        return bean;
    }

    private void assembleHikariDataSource(HikariDataSource dataSource, String persistenceName) {
        Map<String, String> persistenceDatasourceProperties = LeeMultiPersistenceConfiguration
                .getPersistenceDatasourceProperties(persistenceName);
        dataSource.setJdbcUrl(persistenceDatasourceProperties.get(KEY_URL));
        Binder binder = new Binder(new MapConfigurationPropertySource(persistenceDatasourceProperties));
        binder.bind(StringUtils.EMPTY, Bindable.ofInstance(dataSource));
    }

    private void assembleDruidDatasource(DruidDataSource dataSource, String persistenceName) {
        Map<String, String> persistenceDatasourceProperties = LeeMultiPersistenceConfiguration
                .getPersistenceDatasourceProperties(persistenceName);
        Binder binder = new Binder(new MapConfigurationPropertySource(persistenceDatasourceProperties));
        binder.bind(StringUtils.EMPTY, Bindable.ofInstance(dataSource));
    }

    private void assembleTomcatJdbcDatasource(DataSource dataSource, String persistenceName) {
        Map<String, String> persistenceDatasourceProperties = LeeMultiPersistenceConfiguration
                .getPersistenceDatasourceProperties(persistenceName);
        Binder binder = new Binder(new MapConfigurationPropertySource(persistenceDatasourceProperties));
        binder.bind(StringUtils.EMPTY, Bindable.ofInstance(dataSource));
    }

}