package com.mycompany.myapp.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.mycompany.myapp.domain")
@EnableJpaRepositories(
    transactionManagerRef = "partner3TransactionManager",
    entityManagerFactoryRef = "partner3EntityManagerFactory",
    basePackages = {
        "com.mycompany.myapp.repository",
        "com.mycompany.myapp.repository.partner3",
    }
)
@Primary
public class Partner3DatabaseConfiguration {

    @Bean
    @ConfigurationProperties("partner3.datasource")
    public DataSourceProperties partner3DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @LiquibaseDataSource
    @ConfigurationProperties("partner3.datasource")
    public DataSource partner3DataSource() {
        return partner3DataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean(name = "partner3EntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
        EntityManagerFactoryBuilder builder
    ) {
        Properties properties = new Properties();
        properties.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.PostgreSQLDialect"
        );
        properties.setProperty("hibernate.jdbc.batch_size", "25");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.fetch_size", "150");

        LocalContainerEntityManagerFactoryBean emf = builder
            .dataSource(partner3DataSource())
            .packages("com.mycompany.myapp.domain")
            .persistenceUnit("partner3")
            .build();
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "partner3TransactionManager")
    @Primary
    public JpaTransactionManager db2TransactionManager(
        @Qualifier(
            "partner3EntityManagerFactory"
        ) final EntityManagerFactory emf
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
