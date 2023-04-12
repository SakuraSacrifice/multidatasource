package com.lee.multidatasource.autoconfig;

import com.lee.multidatasource.config.LeeMultiPersistenceConfiguration;
import com.lee.multidatasource.postprocessor.DataSourceBeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({LeeMultiPersistenceConfiguration.class, DataSourceBeanPostProcessor.class,})
public class LeeMultiPersistenceAutoConfiguration {}