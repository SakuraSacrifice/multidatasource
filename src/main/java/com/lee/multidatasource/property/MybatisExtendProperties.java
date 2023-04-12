package com.lee.multidatasource.property;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;

public class MybatisExtendProperties extends MybatisProperties {

    private String basePackage;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

}