package com.teame10.healthcare;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Team E10 - Phase 2 Database Connection & Entity Mapping Test
 * Verifies configured database connectivity and validates all 9 JPA entity mappings.
 */
@SpringBootTest
class DatabaseConnectionTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("Verify configured database connectivity")
    void testDatabaseConnection() throws SQLException {
        assertThat(dataSource).isNotNull();

        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(2)).isTrue();

            DatabaseMetaData metaData = connection.getMetaData();
            logger.info("Connected to Database Product: {}", metaData.getDatabaseProductName());
            logger.info("Database Product Version: {}", metaData.getDatabaseProductVersion());
            logger.info("JDBC Driver Name: {}", metaData.getDriverName());
            logger.info("Connected Catalog / Schema: {}", connection.getCatalog());

            String expectedDatabase = environment.acceptsProfiles(Profiles.of("demo"))
                    ? "H2"
                    : "MySQL";
            assertThat(metaData.getDatabaseProductName()).containsIgnoringCase(expectedDatabase);
        }
    }

    @Test
    @DisplayName("Verify All 9 JPA Entities Are Mapped In Metamodel")
    void testAllEntitiesMapped() {
        assertThat(entityManager).isNotNull();

        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        Set<String> entityNames = entities.stream()
                .map(EntityType::getName)
                .collect(Collectors.toSet());

        logger.info("JPA Registered Entities (Count: {}): {}", entityNames.size(), entityNames);

        assertThat(entityNames).contains(
                "User",
                "Department",
                "Doctor",
                "Patient",
                "DoctorSchedule",
                "Appointment",
                "QueueToken",
                "Notification",
                "AuditLog"
        );
    }

    @Test
    @DisplayName("Verify all 9 mapped database tables exist")
    void testAllEntityTablesCreated() throws SQLException {
        Set<String> tableNames = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(
                     connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME").toLowerCase(Locale.ROOT));
            }
        }

        assertThat(tableNames).contains(
                "users",
                "departments",
                "doctors",
                "patients",
                "doctor_schedules",
                "appointments",
                "queue_tokens",
                "notifications",
                "audit_logs"
        );
    }
}
