package com.netflix.nebula.archrules.testingframeworks;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersContainerModuleMapperTest {

    @Test
    public void testStandardNamingConvention() {
        // Standard pattern: remove "Container" suffix and lowercase
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("CassandraContainer"))
                .isEqualTo("org.testcontainers.cassandra");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("KafkaContainer"))
                .isEqualTo("org.testcontainers.kafka");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("Neo4jContainer"))
                .isEqualTo("org.testcontainers.neo4j");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("ElasticsearchContainer"))
                .isEqualTo("org.testcontainers.elasticsearch");
    }

    @Test
    public void testSpecialCases_SqlDatabases() {
        // SQL databases with acronyms
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("PostgreSQLContainer"))
                .isEqualTo("org.testcontainers.postgresql");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("MySQLContainer"))
                .isEqualTo("org.testcontainers.mysql");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("MariaDBContainer"))
                .isEqualTo("org.testcontainers.mariadb");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("MSSQLServerContainer"))
                .isEqualTo("org.testcontainers.mssqlserver");
    }

    @Test
    public void testSpecialCases_NoSqlDatabases() {
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("MongoDBContainer"))
                .isEqualTo("org.testcontainers.mongodb");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("ScyllaDBContainer"))
                .isEqualTo("org.testcontainers.scylladb");
    }

    @Test
    public void testSpecialCases_Infrastructure() {
        // Compound names
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("LocalStackContainer"))
                .isEqualTo("org.testcontainers.localstack");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("MockServerContainer"))
                .isEqualTo("org.testcontainers.mockserver");
    }

    @Test
    public void testSpecialCases_NumbersAndAbbreviations() {
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("K3sContainer"))
                .isEqualTo("org.testcontainers.k3s");
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("K6Container"))
                .isEqualTo("org.testcontainers.k6");
    }

    @Test
    public void testInvalidInput() {
        // Non-container classes return null
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName("SomeRandomClass"))
                .isNull();
        assertThat(TestcontainersContainerModuleMapper.getModulePackageName(""))
                .isNull();
    }

    @Test
    public void testDeriveModuleName() {
        // Test the module name derivation (without "org.testcontainers." prefix)
        assertThat(TestcontainersContainerModuleMapper.deriveModuleName("PostgreSQLContainer"))
                .isEqualTo("postgresql");
        assertThat(TestcontainersContainerModuleMapper.deriveModuleName("CassandraContainer"))
                .isEqualTo("cassandra");
        assertThat(TestcontainersContainerModuleMapper.deriveModuleName("SomeClass"))
                .isNull();
    }
}
