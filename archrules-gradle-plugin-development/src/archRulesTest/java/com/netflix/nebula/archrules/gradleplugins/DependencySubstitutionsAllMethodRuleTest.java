package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.artifacts.DependencySubstitutions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencySubstitutionsAllMethodRuleTest {
    @Test
    public void pluginNotUsingDeprecatedApis_should_fail() {
        final EvaluationResult result = Runner.check(
                DependencySubstitutionsAllMethodRule.DEPENDENCYSUBSTITUTIONS_ALL,
                DependencySubstitutionsAllMethodRuleTest.CodeUsingAll.class
        );
        assertThat(result.hasViolation()).isTrue();
    }

    static class CodeUsingAll {
        public void bad(DependencySubstitutions substitutions) {
            substitutions.all((substitution) -> {

            });
        }
    }
}
