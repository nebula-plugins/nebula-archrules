package com.netflix.nebula.archrules.testingframeworks;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for mapping Testcontainers container class names to their 2.x module package names.
 * <p>
 * In Testcontainers 2.x, container classes moved from {@code org.testcontainers.containers} to
 * module-specific packages. This class provides the mapping logic to determine the new package location
 * for a given container class name.
 * <p>
 * Example mappings:
 * <ul>
 *   <li>{@code PostgreSQLContainer} → {@code org.testcontainers.postgresql}</li>
 *   <li>{@code CassandraContainer} → {@code org.testcontainers.cassandra}</li>
 *   <li>{@code KafkaContainer} → {@code org.testcontainers.kafka}</li>
 * </ul>
 */
@NullMarked
public class TestcontainersContainerModuleMapper {

    /**
     * Lazy-initialized holder for the overrides map.
     * <p>
     * Map of container class names to their module names for cases that don't follow the standard naming pattern.
     * Standard pattern: Remove "Container" suffix and convert to lowercase.
     * <br>Example: CassandraContainer → cassandra, KafkaContainer → kafka
     * <p>
     * Special cases are needed when:
     * <ul>
     *   <li>Acronyms/abbreviations (MySQL → mysql, not mysqlcontainer)</li>
     *   <li>Multi-word names (LocalStack → localstack, not localstackcontainer)</li>
     *   <li>Version-specific modules (oracle-xe vs oracle-free)</li>
     *   <li>Numeric suffixes (K3s, K6)</li>
     * </ul>
     */
    private static class OverridesHolder {
        private static final Map<String, String> INSTANCE;

        static {
            Map<String, String> map = new HashMap<>();
            map.put("PostgreSQLContainer", "postgresql");
            map.put("MySQLContainer", "mysql");
            map.put("MariaDBContainer", "mariadb");
            map.put("MSSQLServerContainer", "mssqlserver");
            map.put("MongoDBContainer", "mongodb");
            map.put("ScyllaDBContainer", "scylladb");
            map.put("OracleContainer", "oracle-xe");
            map.put("LocalStackContainer", "localstack");
            map.put("MockServerContainer", "mockserver");
            map.put("OpenFGAContainer", "openfga");
            map.put("HiveMQContainer", "hivemq");
            map.put("K3sContainer", "k3s");
            map.put("K6Container", "k6");
            map.put("TiDBContainer", "tidb");
            map.put("OceanBaseContainer", "oceanbase");
            map.put("QdrantContainer", "qdrant");
            map.put("QuestDBContainer", "questdb");
            map.put("R2DBCDatabaseContainer", "r2dbc");
            INSTANCE = map;
        }
    }

    private static Map<String, String> getOverrides() {
        return OverridesHolder.INSTANCE;
    }

    /**
     * Gets the full module package name for a container class.
     *
     * @param containerClassName The simple name of the container class (e.g., "PostgreSQLContainer")
     * @return The full package name (e.g., "org.testcontainers.postgresql"), or null if unknown
     */
    @Nullable
    public static String getModulePackageName(String containerClassName) {
        String moduleName = deriveModuleName(containerClassName);
        return moduleName != null ? "org.testcontainers." + moduleName : null;
    }

    /**
     * Derives the module name from a container class name.
     * First checks OVERRIDES, then applies standard heuristic.
     *
     * @param containerClassName The simple name of the container class
     * @return The module name (e.g., "postgresql", "cassandra"), or null if unknown
     */
    @Nullable
    public static String deriveModuleName(String containerClassName) {
        // Check explicit overrides for special cases
        Map<String, String> overrides = getOverrides();
        if (overrides.containsKey(containerClassName)) {
            return overrides.get(containerClassName);
        }

        // Apply standard heuristic: remove "Container" suffix and lowercase
        return applyStandardNamingConvention(containerClassName);
    }

    /**
     * Applies standard naming convention: ContainerName → lowercase(name without "Container").
     * Example: CassandraContainer → cassandra, KafkaContainer → kafka
     *
     * @param containerClassName The simple name of the container class
     * @return The module name, or null if the class name doesn't end with "Container"
     */
    @Nullable
    static String applyStandardNamingConvention(String containerClassName) {
        if (!containerClassName.endsWith("Container")) {
            return null;
        }
        int suffixLength = "Container".length();
        return containerClassName.substring(0, containerClassName.length() - suffixLength).toLowerCase();
    }
}
