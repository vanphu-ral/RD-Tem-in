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
@EntityScan(basePackages = "com.mycompany.panacimmc.domain")
@EnableJpaRepositories(
    transactionManagerRef = "partnerTransactionManager",
    entityManagerFactoryRef = "partnerEntityManagerFactory",
    basePackages = "com.mycompany.panacimmc.repository"
)
public class PartnerDatabaseConfiguration {

    @Bean
    @ConfigurationProperties("partner.datasource")
    public DataSourceProperties partnerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("partner.datasource")
    public DataSource partnerDataSource() {
        return partnerDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "partnerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        LocalContainerEntityManagerFactoryBean emf = builder
            .dataSource(partnerDataSource())
            .packages("com.mycompany.panacimmc.domain")
            .persistenceUnit("partner")
            .build();
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "partnerTransactionManager")
    public JpaTransactionManager db2TransactionManager(@Qualifier("partnerEntityManagerFactory") final EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
