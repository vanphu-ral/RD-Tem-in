package com.mycompany.myapp.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(
    basePackages = { "com.mycompany.wh.domain", "com.mycompany.myapp.domain" }
)
@EnableJpaRepositories(
    transactionManagerRef = "partner6TransactionManager",
    entityManagerFactoryRef = "partner6EntityManagerFactory",
    basePackages = {
        "com.mycompany.wh.repository",
        "com.mycompany.myapp.repository.partner6",
    }
)
public class Partner6DatabaseConfiguration {

    @Bean
    @ConfigurationProperties("partner6.datasource")
    public DataSourceProperties partner6DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("partner6.datasource")
    public DataSource partner6DataSource() {
        return partner6DataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean(name = "partner6EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
        EntityManagerFactoryBuilder builder
    ) {
        Properties properties = new Properties();
        properties.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.SQLServerDialect"
        );

        LocalContainerEntityManagerFactoryBean emf = builder
            .dataSource(partner6DataSource())
            .packages("com.mycompany.wh.domain", "com.mycompany.myapp.domain")
            .persistenceUnit("partner6")
            .build();
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "partner6TransactionManager")
    public JpaTransactionManager db2TransactionManager(
        @Qualifier(
            "partner6EntityManagerFactory"
        ) final EntityManagerFactory emf
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
