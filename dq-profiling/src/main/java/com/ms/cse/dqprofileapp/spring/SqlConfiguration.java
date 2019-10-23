package com.ms.cse.dqprofileapp.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Component
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "sqlEntityManagerFactory",
        transactionManagerRef = "sqlTransactionManager",
        basePackages = { "com.ms.cse.dqprofileapp.repositories" })
public class SqlConfiguration {

    @Bean(name = "sqlDataSourceConfiguration")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties sqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "sqlEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean
    sqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
             DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.ms.cse.dqprofileapp.models")
                .persistenceUnit("primaryPU")
                .build();
    }

    @Bean(name = "sqlTransactionManager")
    @Primary
    public PlatformTransactionManager sqlTransactionManager(
            @Qualifier("sqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
