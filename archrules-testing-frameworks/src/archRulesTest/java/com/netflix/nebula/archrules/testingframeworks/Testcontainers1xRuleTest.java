package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class Testcontainers1xRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(Testcontainers1xRuleTest.class);

    @Test
    public void dockerComposeContainer_should_fail() {
        final EvaluationResult result = Runner.check(
                Testcontainers1xRule.dockerComposeContainerRule,
                UsesDockerComposeContainer.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void composeContainer_should_pass() {
        final EvaluationResult result = Runner.check(
                Testcontainers1xRule.dockerComposeContainerRule,
                UsesComposeContainer.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void getContainerIpAddress_should_fail() {
        final EvaluationResult result = Runner.check(
                Testcontainers1xRule.containerIpAddressMethodRule,
                UsesGetContainerIpAddress.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void getHost_should_pass() {
        final EvaluationResult result = Runner.check(
                Testcontainers1xRule.containerIpAddressMethodRule,
                UsesGetHost.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    // Test classes using 1.x patterns
    @SuppressWarnings("unused")
    public static class UsesDockerComposeContainer {
        private org.testcontainers.containers.DockerComposeContainer<?> container;
    }

    @SuppressWarnings("unused")
    public static class UsesComposeContainer {
        private org.testcontainers.containers.ComposeContainer container;
    }

    @SuppressWarnings("unused")
    public static class UsesGetContainerIpAddress {
        public void getIp(org.testcontainers.containers.ContainerState container) {
            String ip = container.getContainerIpAddress();
        }
    }

    @SuppressWarnings("unused")
    public static class UsesGetHost {
        public void getIp(org.testcontainers.containers.ContainerState container) {
            String host = container.getHost();
        }
    }
}
