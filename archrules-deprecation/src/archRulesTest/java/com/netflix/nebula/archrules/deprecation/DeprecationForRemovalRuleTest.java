package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.Priority;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeprecationForRemovalRuleTest {
    @Test
    public void testDeprecationForRemovalRule_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, DeprecationForRemovalRuleTest.CodeThatUsesDeprecatedForRemovalClass.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    public void testDeprecationForRemovalRule_method() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, DeprecationForRemovalRuleTest.CodeThatUsesDeprecatedForRemovalMethod.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    public void testRegularDeprecated() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, DeprecationForRemovalRuleTest.CodeThatUsesRegularDeprecatedClass.class);
        assertThat(result.hasViolation()).isFalse();
    }

    @Deprecated(forRemoval = true)
    static class DeprecatedForRemovalClass {
        static void method() {}
    }

    static class DeprecatedForRemovalMethod {
        @Deprecated(forRemoval = true)
        static void deprecated() {}
    }

    @Deprecated
    static class RegularDeprecatedClass {
        static void method() {}
    }

    static class CodeThatUsesDeprecatedForRemovalClass {
        static void usage() {
            DeprecatedForRemovalClass.method();
        }
    }

    static class CodeThatUsesDeprecatedForRemovalMethod {
        static void usage() {
            DeprecatedForRemovalMethod.deprecated();
        }
    }

    static class CodeThatUsesRegularDeprecatedClass {
        static void usage() {
            RegularDeprecatedClass.method();
        }
    }
}
