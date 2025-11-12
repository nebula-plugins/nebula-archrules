package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnit4RuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(JUnit4RuleTest.class);

    @Test
    public void junit4_test_package_test() {
        final EvaluationResult result = Runner.check(JUnit4Rule.junit4Rule, JUnit4Usage.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void junit5_test_package_test() {
        final EvaluationResult result = Runner.check(JUnit4Rule.junit4Rule, JUnit5Usage.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void junit4_extends_resource_test() {
        final EvaluationResult result = Runner.check(JUnit4Rule.junit4Rule, JUnit4TestRule.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    public static class JUnit4Usage {
        @org.junit.Test
        public void test() { }
    }

    public static class JUnit5Usage {
        @org.junit.jupiter.api.Test
        public void test() { }
    }

    public static class JUnit4TestRule extends ExternalResource {

    }
}
