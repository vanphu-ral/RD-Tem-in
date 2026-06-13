package com.mycompany.myapp.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnProperty(
    prefix = "partner7",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class Partner7DatabaseConfiguration {

    @Bean
    @ConfigurationProperties("partner7.datasource")
    public DataSourceProperties partner7DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("partner7.datasource")
    public DataSource partner7DataSource() {
        return partner7DataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean(name = "partner7JdbcTemplate")
    public JdbcTemplate partner7JdbcTemplate(
        @Qualifier("partner7DataSource") DataSource partner7DataSource
    ) {
        return new JdbcTemplate(partner7DataSource);
    }
}
