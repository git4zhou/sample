package com.spectre.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.spectre.entity")
@PropertySource(value = "classpath:application.properties")
public class DatabaseConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.driver:oracle.jdbc.driver.OracleDriver}")
    private String driverClassName;

    @Value("${jdbc.initialSize:10}")
    private int initialSize;

    @Value("${jdbc.minIdle:4}")
    private int minIdle;

    @Value("${jdbc.maxActive:50}")
    private int maxActive;

    @Value("${jdbc.maxWait:10000}")
    private int maxWait;

    @Value("${jdbc.timeBetweenEvictionRunsMillis:10000}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${jdbc.minEvictableIdleTimeMillis:30100}")
    private int minEvictableIdleTimeMillis;

    @Value("${jdbc.validationQuery:select 1 FROM DUAL}")
    private String validationQuery;

    @Value("${jdbc.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${jdbc.testOnBorrow:true}")
    private boolean testOnBorrow;

    @Value("${jdbc.testOnReturn:true}")
    private boolean testOnReturn;

    @Value("${jdbc.connectionProperties:config.decrypt=true}")
    private String connectionProperties;

    @Bean
    public DruidDataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();

        return dataSource;
    }

}
