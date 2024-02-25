package com.lee.multidatasource.util;

public class BeanNameUtil {

    private static final String SQL_SESSION_FACTORY_SUFFIX = "SqlSessionFactory";
    private static final String MAPPER_SCANNER_CONFIGURER = "MapperScannerConfigurer";
    private static final String TRANSACTIONAL_MANAGER = "TransactionManager";

    public static String getSqlSessionFactoryName(String originName) {
        return originName + SQL_SESSION_FACTORY_SUFFIX;
    }

    public static String getMapperScannerConfigurerName(String originName) {
        return originName + MAPPER_SCANNER_CONFIGURER;
    }

    public static String getTransactionManagerName(String originName) {
        return originName + TRANSACTIONAL_MANAGER;
    }

}