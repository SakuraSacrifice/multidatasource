package com.lee.multidatasource.property;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lee.multidatasource.constant.InitializeConstants.HASH_MAP_INITIAL_SIZE;

public class MultiPersistenceProperties {

    private final Map<String, PersistenceProperties> persistencePropertiesMap = new HashMap<>(HASH_MAP_INITIAL_SIZE);

    public void addPersistenceProperties(String persistenceName,
                                         DataSourceProperties dataSourceProperties,
                                         MybatisExtendProperties mybatisProperties) {
        PersistenceProperties persistenceProperties = new PersistenceProperties(dataSourceProperties, mybatisProperties);
        persistencePropertiesMap.put(persistenceName, persistenceProperties);
    }

    public List<String> getPersistenceNames() {
        return new ArrayList<>(persistencePropertiesMap.keySet());
    }

    public PersistenceProperties getPersistenceProperties(String persistenceName) {
        return persistencePropertiesMap.get(persistenceName);
    }

    public DataSourceProperties getDataSourceProperties(String persistenceName) {
        PersistenceProperties persistenceProperties = persistencePropertiesMap.get(persistenceName);
        if (ObjectUtils.isNotEmpty(persistenceProperties)) {
            return persistenceProperties.getDataSourceProperties();
        }
        throw new RuntimeException();
    }

    public MybatisExtendProperties getMybatisProperties(String persistenceName) {
        PersistenceProperties persistenceProperties = persistencePropertiesMap.get(persistenceName);
        if (ObjectUtils.isNotEmpty(persistenceProperties)) {
            return persistenceProperties.getMybatisProperties();
        }
        throw new RuntimeException();
    }

    public static class PersistenceProperties {
        private DataSourceProperties dataSourceProperties;
        private MybatisExtendProperties mybatisProperties;

        public PersistenceProperties(DataSourceProperties dataSourceProperties,
                                     MybatisExtendProperties mybatisProperties) {
            this.dataSourceProperties = dataSourceProperties;
            this.mybatisProperties = mybatisProperties;
        }

        public DataSourceProperties getDataSourceProperties() {
            return dataSourceProperties;
        }

        public void setDataSourceProperties(DataSourceProperties dataSourceProperties) {
            this.dataSourceProperties = dataSourceProperties;
        }

        public MybatisExtendProperties getMybatisProperties() {
            return mybatisProperties;
        }

        public void setMybatisProperties(MybatisExtendProperties mybatisProperties) {
            this.mybatisProperties = mybatisProperties;
        }
    }

}