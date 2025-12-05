package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.Priority;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeprecationForRemovalRuleTest {
    @Test
    public void testDeprecationForRemovalRule_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, DeprecationForRemovalRuleTest.CodeThatUsesDeprecatedForRemovalClass.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority() == Priority.MEDIUM);
    }

    @Deprecated(forRemoval = true)
    static class DeprecatedForRemovalClass {
        static void deprecated() {}
    }

    static class CodeThatUsesDeprecatedForRemovalClass {
        static void usage() {
            DeprecatedForRemovalClass.deprecated();
        }
    }
}
