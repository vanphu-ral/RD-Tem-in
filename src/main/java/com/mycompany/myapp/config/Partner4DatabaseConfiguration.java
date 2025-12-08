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
@EntityScan(basePackages = "com.mycompany.wh.domain")
@EnableJpaRepositories(
    transactionManagerRef = "partner4TransactionManager",
    entityManagerFactoryRef = "partner4EntityManagerFactory",
    basePackages = "com.mycompany.whh.repository"
)
public class Partner4DatabaseConfiguration {

    @Bean
    @ConfigurationProperties("partner4.datasource")
    public DataSourceProperties partner4DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("partner4.datasource")
    public DataSource partner4DataSource() {
        return partner4DataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean(name = "partner4EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
        EntityManagerFactoryBuilder builder
    ) {
        Properties properties = new Properties();
        properties.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.SQLServerDialect"
        );

        LocalContainerEntityManagerFactoryBean emf = builder
            .dataSource(partner4DataSource())
            .packages("com.mycompany.whh.domain")
            .persistenceUnit("partner4")
            .build();
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "partner4TransactionManager")
    public JpaTransactionManager db2TransactionManager(
        @Qualifier(
            "partner4EntityManagerFactory"
        ) final EntityManagerFactory emf
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
