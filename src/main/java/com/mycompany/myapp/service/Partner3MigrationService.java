package com.mycompany.myapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for running database migrations on partner3 database.
 */
@Service
public class Partner3MigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(
        Partner3MigrationService.class
    );

    private final DataSource partner3DataSource;

    public Partner3MigrationService(
        @Qualifier("partner3DataSource") DataSource partner3DataSource
    ) {
        this.partner3DataSource = partner3DataSource;
    }

    /**
     * Run liquibase migrations on partner3 database.
     */
    public void runMigrations() {
        LOG.info("Starting partner3 database migrations");

        try (Connection connection = partner3DataSource.getConnection()) {
            Database database =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(connection)
                );

            Liquibase liquibase = new Liquibase(
                "config/liquibase/partner3/master.xml",
                new ClassLoaderResourceAccessor(),
                database
            );

            liquibase.update(new Contexts(), new LabelExpression());

            LOG.info("Partner3 database migrations completed successfully");
        } catch (Exception e) {
            LOG.error("Failed to run partner3 database migrations", e);
            throw new RuntimeException("Partner3 database migration failed", e);
        }
    }

    /**
     * Create pallet_infor_detail table manually if liquibase fails.
     */
    public void createPalletTableManually() {
        LOG.info("Creating pallet_infor_detail table manually");

        String createTableSQL =
            "CREATE TABLE IF NOT EXISTS pallet_infor_detail (" +
            "id SERIAL PRIMARY KEY, " +
            "serial_pallet VARCHAR(50) NOT NULL, " +
            "ma_lenh_san_xuat_id BIGINT, " +
            "scan_progress INTEGER, " +
            "num_box_actual INTEGER, " +
            "updated_at TIMESTAMP, " +
            "updated_by VARCHAR(50)" +
            ");";

        try (
            Connection connection = partner3DataSource.getConnection();
            Statement statement = connection.createStatement();
        ) {
            statement.execute(createTableSQL);
            LOG.info("pallet_infor_detail table created successfully");
        } catch (SQLException e) {
            LOG.error("Failed to create pallet_infor_detail table", e);
            throw new RuntimeException("Manual table creation failed", e);
        }
    }
}
