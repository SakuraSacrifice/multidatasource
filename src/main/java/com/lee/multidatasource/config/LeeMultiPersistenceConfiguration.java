package com.lee.multidatasource.config;

import com.lee.multidatasource.entity.MultiPersistencePropertiesWrapper;
import com.lee.multidatasource.property.MultiPersistenceProperties;
import com.lee.multidatasource.property.MybatisExtendProperties;
import com.lee.multidatasource.util.BeanNameUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;
import java.util.Map;

import static com.lee.multidatasource.constant.CommonConstants.KEY_DATASOURCE;
import static com.lee.multidatasource.constant.CommonConstants.PERSISTENCE_PREFIX;
import static com.lee.multidatasource.constant.PropertyFieldConstants.*;

public class LeeMultiPersistenceConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(LeeMultiPersistenceConfiguration.class);

    private static final Class<HikariDataSource> DEFAULT_DATASOURCE_CLASS = HikariDataSource.class;

    private static Map<String, Map<String, Map<String, String>>> persistencePropertiesCache;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        MultiPersistenceProperties multiPersistenceProperties = parseMultiPersistenceProperties();
        List<String> persistenceNames = multiPersistenceProperties.getPersistenceNames();
        for (String persistenceName : persistenceNames) {
            registerDatasource(registry, persistenceName, multiPersistenceProperties.getDataSourceProperties(persistenceName));
            registerSqlSessionFactory(registry, persistenceName, multiPersistenceProperties.getMybatisProperties(persistenceName));
            registerMapperScannerConfigurer(registry, persistenceName, multiPersistenceProperties.getMybatisProperties(persistenceName));
            registerTransactionManager(registry, persistenceName);
        }
    }

    private MultiPersistenceProperties parseMultiPersistenceProperties() {
        MultiPersistenceProperties multiPersistenceProperties = new MultiPersistenceProperties();
        MultiPersistencePropertiesWrapper multiPersistencePropertiesWrapper = parseMultiPersistencePropertiesWrapper();
        List<String> persistenceNames = multiPersistencePropertiesWrapper.getPersistenceNames();
        for (String persistenceName : persistenceNames) {
            DataSourceProperties dataSourceProperties = multiPersistencePropertiesWrapper
                    .getPersistenceDataSourceProperties(persistenceName);
            MybatisExtendProperties mybatisProperties = multiPersistencePropertiesWrapper
                    .getPersistenceMybatisProperties(persistenceName);
            multiPersistenceProperties.addPersistenceProperties(
                    persistenceName, dataSourceProperties, mybatisProperties);
        }
        return multiPersistenceProperties;
    }

    private MultiPersistencePropertiesWrapper parseMultiPersistencePropertiesWrapper() {
        Map<String, Map<String, Map<String, String>>> persistenceProperties;

        Binder binder = Binder.get(environment);
        persistenceProperties = binder.bind(PERSISTENCE_PREFIX, Bindable.of(Map.class)).get();
        persistencePropertiesCache = persistenceProperties;

        return new MultiPersistencePropertiesWrapper(persistenceProperties);
    }

    private void registerDatasource(BeanDefinitionRegistry registry,
                                    String persistenceName,
                                    DataSourceProperties dataSourceProperties) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(
                ObjectUtils.isNotEmpty(dataSourceProperties.getType()) ? dataSourceProperties.getType() : DEFAULT_DATASOURCE_CLASS);

        registry.registerBeanDefinition(persistenceName, beanDefinitionBuilder.getBeanDefinition());
    }

    private void registerSqlSessionFactory(BeanDefinitionRegistry registry,
                                           String persistenceName,
                                           MybatisExtendProperties mybatisProperties) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(SqlSessionFactoryBean.class);
        beanDefinitionBuilder.addPropertyReference(DATA_SOURCE, persistenceName);
        beanDefinitionBuilder.addPropertyValue(CONFIG_LOCATION, mybatisProperties.getConfigLocation());
        registry.registerBeanDefinition(BeanNameUtil.getSqlSessionFactoryName(persistenceName),
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void registerMapperScannerConfigurer(BeanDefinitionRegistry registry,
                                                 String persistenceName,
                                                 MybatisExtendProperties mybatisProperties) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(MapperScannerConfigurer.class);
        beanDefinitionBuilder.addPropertyValue(SQL_SESSION_FACTORY_BEANNAME, BeanNameUtil.getSqlSessionFactoryName(persistenceName));
        beanDefinitionBuilder.addPropertyValue(BASE_PACKAGE, mybatisProperties.getBasePackage());
        registry.registerBeanDefinition(BeanNameUtil.getMapperScannerConfigurerName(persistenceName),
                beanDefinitionBuilder.getBeanDefinition());
    }

    private void registerTransactionManager(BeanDefinitionRegistry registry,
                                            String persistenceName) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(DataSourceTransactionManager.class);
        beanDefinitionBuilder.addPropertyReference(DATA_SOURCE, persistenceName);
        registry.registerBeanDefinition(BeanNameUtil.getTransactionManagerName(persistenceName),
                beanDefinitionBuilder.getBeanDefinition());
    }

    public static Map<String, String> getPersistenceDatasourceProperties(String persistenceName) {
        Map<String, Map<String, String>> persistenceProperties = persistencePropertiesCache.get(persistenceName);
        return persistenceProperties.get(KEY_DATASOURCE);
    }

}