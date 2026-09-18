package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Project;
import org.gradle.kotlin.dsl.KotlinDependencyExtensionsKt;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GradlePluginNoDslRuleTest {
    @Test
    void test() {
        final EvaluationResult result = Runner.check(
                GradlePluginNoDslRule.DONT_USE_KOTLIN_DSL,
                GradlePluginNoDslRuleTest.Failing.class
        );
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("The kotlin-dsl plugin should not be used in binary Gradle plugin projects.");
        assertThat(result.getFailureReport().toString()).contains("Use built-in Gradle APIs instead.");
    }

    static class Failing {
        void action() {
            Project project = ProjectBuilder.builder().build();
            KotlinDependencyExtensionsKt.embeddedKotlin(project.getDependencies(), "reflect");
        }
    }
}
