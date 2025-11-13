package com.netflix.nebula.archrules.joda;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class JodaRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(JodaRuleTest.class);

    @Test
    public void test_joda_variable_usage() {
        final EvaluationResult result = Runner.check(JodaRule.jodaRule, JodaUsageVariable.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void test_joda_parameter_usage() {
        final EvaluationResult result = Runner.check(JodaRule.jodaRule, JodaUsageParameter.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void test_java_time_usage() {
        final EvaluationResult result = Runner.check(JodaRule.jodaRule, JavaTimeUsage.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    public static class JodaUsageVariable {
        private org.joda.time.DateTime dateTime;

        public void useJodaTime() {
            dateTime = org.joda.time.DateTime.now();
        }
    }

    public static class JodaUsageParameter {
        public void useJodaTime(org.joda.time.DateTime dateTime) { }
    }

    public static class JavaTimeUsage {
        private java.time.LocalDateTime dateTime;

        public void useJavaTime() {
            dateTime = java.time.LocalDateTime.now();
        }
    }
}
