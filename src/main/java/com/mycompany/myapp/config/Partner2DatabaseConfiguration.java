package com.mycompany.myapp.config;

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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.mycompany.wmsral.domain")
@EnableJpaRepositories(
    transactionManagerRef = "partner2TransactionManager",
    entityManagerFactoryRef = "partner2EntityManagerFactory",
    basePackages = "com.mycompany.wmsral.repository"
)
public class Partner2DatabaseConfiguration {

    @Bean
    @ConfigurationProperties("partner2.datasource")
    public DataSourceProperties partner2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("partner2.datasource")
    public DataSource partner2DataSource() {
        return partner2DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "partner2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        LocalContainerEntityManagerFactoryBean emf = builder
            .dataSource(partner2DataSource())
            .packages("com.mycompany.wmsral.domain")
            .persistenceUnit("partner2")
            .build();
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "partner2TransactionManager")
    public JpaTransactionManager db2TransactionManager(@Qualifier("partner2EntityManagerFactory") final EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}

