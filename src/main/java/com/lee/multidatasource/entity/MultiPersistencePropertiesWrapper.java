package com.lee.multidatasource.entity;

import com.lee.multidatasource.property.MybatisExtendProperties;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lee.multidatasource.constant.CommonConstants.KEY_DATASOURCE;
import static com.lee.multidatasource.constant.CommonConstants.KEY_MYBATIS;

public class MultiPersistencePropertiesWrapper {

    private Map<String, Map<String, Map<String, String>>> multiPersistenceProperties;

    public MultiPersistencePropertiesWrapper(Map<String, Map<String, Map<String, String>>> multiPersistenceProperties) {
        this.multiPersistenceProperties = multiPersistenceProperties;
    }

    public void setMultiPersistenceProperties(Map<String, Map<String, Map<String, String>>> multiPersistenceProperties) {
        this.multiPersistenceProperties = multiPersistenceProperties;
    }

    public int getPersistenceSize() {
        return multiPersistenceProperties.size();
    }

    public List<String> getPersistenceNames() {
        return new ArrayList<>(multiPersistenceProperties.keySet());
    }

    public DataSourceProperties getPersistenceDataSourceProperties(String persistenceName) {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        Map<String, Map<String, String>> persistenceProperties = multiPersistenceProperties.get(persistenceName);
        Map<String, String> persistenceDatasourceProperties = persistenceProperties.get(KEY_DATASOURCE);
        if (ObjectUtils.isNotEmpty(persistenceDatasourceProperties) || !persistenceDatasourceProperties.isEmpty()) {
            Binder binder = new Binder(new MapConfigurationPropertySource(persistenceDatasourceProperties));
            dataSourceProperties = binder.bind(StringUtils.EMPTY, Bindable.of(DataSourceProperties.class)).get();
        }
        return dataSourceProperties;
    }

    public MybatisExtendProperties getPersistenceMybatisProperties(String persistenceName) {
        MybatisExtendProperties mybatisProperties = new MybatisExtendProperties();
        Map<String, Map<String, String>> persistenceProperties = multiPersistenceProperties.get(persistenceName);
        Map<String, String> persistenceMybatisProperties = persistenceProperties.get(KEY_MYBATIS);
        if (ObjectUtils.isNotEmpty(persistenceMybatisProperties) && !persistenceMybatisProperties.isEmpty()) {
            Binder binder = new Binder(new MapConfigurationPropertySource(persistenceMybatisProperties));
            mybatisProperties = binder.bind(StringUtils.EMPTY, Bindable.of(MybatisExtendProperties.class)).get();
        }
        return mybatisProperties;
    }

}