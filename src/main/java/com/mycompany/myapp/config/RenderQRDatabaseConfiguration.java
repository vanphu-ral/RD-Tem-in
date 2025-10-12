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
@EntityScan(basePackages = "com.mycompany.renderQr.domain")
@EnableJpaRepositories(
    transactionManagerRef = "renderqrTransactionManager",
    entityManagerFactoryRef = "renderqrEntityManagerFactory",
    basePackages = "com.mycompany.renderQr.repository"
)
public class RenderQRDatabaseConfiguration {

    @Bean
    @ConfigurationProperties("renderqr.datasource")
    public DataSourceProperties renderqrDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("renderqr.datasource")
    public DataSource renderqrDataSource() {
        return renderqrDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean(name = "renderqrEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean renderqrEntityManagerFactory(
        EntityManagerFactoryBuilder builder
    ) {
        Properties properties = new Properties();
        properties.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.SQLServerDialect"
        );

        LocalContainerEntityManagerFactoryBean emf = builder
            .dataSource(renderqrDataSource())
            .packages("com.mycompany.renderQr.domain")
            .persistenceUnit("renderqr")
            .build();
        emf.setJpaProperties(properties);
        return emf;
    }

    @Bean(name = "renderqrTransactionManager")
    public JpaTransactionManager renderqrTransactionManager(
        @Qualifier("renderqrEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
