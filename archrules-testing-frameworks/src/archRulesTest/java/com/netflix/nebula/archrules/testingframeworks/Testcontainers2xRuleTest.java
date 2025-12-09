package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

public class Testcontainers2xRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(Testcontainers2xRuleTest.class);

    @Test
    public void legacyContainerPackage_on_1x_should_pass() {
        // On Testcontainers 1.x, only org.testcontainers.containers exists
        // This rule should stay silent since new packages don't exist yet
        final EvaluationResult result = Runner.check(
                Testcontainers2xRule.legacyContainerPackageRule,
                UsesOldPackageLocation.class
        );
        LOG.info(result.getFailureReport().toString());
        // On 1.x this should pass (no violation) because new packages don't exist
        // On 2.x this would fail (has violation) because new packages are available
        assertThat(result.hasViolation()).isFalse();
    }

    // Test class using old package location
    @SuppressWarnings("unused")
    public static class UsesOldPackageLocation {
        // Using old package location that's deprecated in 2.x
        private PostgreSQLContainer<?> container;

        public UsesOldPackageLocation() {
            this.container = new org.testcontainers.containers.PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:16-alpine")
            );
        }
    }
}
