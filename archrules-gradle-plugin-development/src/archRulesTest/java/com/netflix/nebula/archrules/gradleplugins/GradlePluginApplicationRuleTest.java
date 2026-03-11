package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GradlePluginApplicationRuleTest {
    @Test
    public void test_plugins() {
        final EvaluationResult result = Runner.check(
                GradlePluginApplicationRule.APPLY_BY_ID,
                GradlePluginApplicationRuleTest.PluginA.class,
                GradlePluginApplicationRuleTest.PluginB.class
        );

        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().getDetails()).hasSize(1);
        assertThat(result.getFailureReport().toString())
                .contains("no classes should call method PluginContainer.apply(Class)")
                .contains("or should call method PluginManager.apply(Class)");
    }

    @Test
    public void test_pluginManager() {
        final EvaluationResult result = Runner.check(
                GradlePluginApplicationRule.APPLY_BY_ID,
                GradlePluginApplicationRuleTest.PluginA.class,
                GradlePluginApplicationRuleTest.PluginC.class
        );

        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().getDetails()).hasSize(1);
        assertThat(result.getFailureReport().toString())
                .contains("no classes should call method PluginContainer.apply(Class)")
                .contains("or should call method PluginManager.apply(Class)");
    }

    class PluginA implements Plugin<Project> {
        @Override
        public void apply(Project target) {
            target.getPlugins().apply("base");
            target.getPluginManager().apply("java");
        }
    }

    class PluginB implements Plugin<Project> {
        @Override
        public void apply(Project target) {
            target.getPlugins().apply(PluginA.class);
        }
    }

    class PluginC implements Plugin<Project> {
        @Override
        public void apply(Project target) {
            target.getPluginManager().apply(PluginA.class);
        }
    }
}
