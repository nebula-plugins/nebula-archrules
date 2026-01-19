package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.Runner;
import com.netflix.nebula.archrules.deprecation.other.DeprecatedSinceKotlinClass;
import com.netflix.nebula.archrules.deprecation.other.KotlinDeprecatedClass;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.Priority;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class KotlinDeprecationRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(DeprecationRuleTest.class);

    @Test
    public void test_kotlin_deprecated_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, KotlinDeprecationRuleTest.CodeThatUsesKotlinDeprecatedClass.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
    }

    @Test
    public void test_deprecated_since_kotlin_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, KotlinDeprecationRuleTest.CodeThatUsesDeprecatedSinceKotlinClass.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
    }

    static class CodeThatUsesKotlinDeprecatedClass {
        static void usage() {
            KotlinDeprecatedClass.method();
        }
    }

    static class CodeThatUsesDeprecatedSinceKotlinClass {
        static void usage() {
            DeprecatedSinceKotlinClass.method();
        }
    }
}
