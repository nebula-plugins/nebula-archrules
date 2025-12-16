package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.FakeDeprecatedGradleClass;
import org.gradle.FakeDeprecatedGradleMethod;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleDeprecatedApiRuleTest {
    @Test
    public void pluginNotUsingDeprecatedApis_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleDeprecatedApiRule.pluginsShouldNotUseDeprecatedGradleApis,
                PluginUsingDeprecatedApis.class
        );
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void pluginNotUsingDeprecatedApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleDeprecatedApiRule.pluginsShouldNotUseDeprecatedGradleApis,
                PluginNotUsingDeprecatedApis.class
        );
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskUsingDeprecatedApis_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleDeprecatedApiRule.tasksShouldNotUseDeprecatedGradleApis,
                TaskUsingDeprecatedClass.class
        );
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void taskNotUsingDeprecatedApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleDeprecatedApiRule.tasksShouldNotUseDeprecatedGradleApis,
                TaskNotUsingDeprecatedApis.class
        );
        assertThat(result.hasViolation()).isFalse();
    }

    public static class PluginNotUsingDeprecatedApis implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask", task -> {
                task.setGroup("custom");
                task.setDescription("My custom task");
            });
        }
    }

    public static class PluginUsingDeprecatedApis implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            FakeDeprecatedGradleMethod.aMethod();
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskNotUsingDeprecatedApis extends DefaultTask {
        @TaskAction
        public void execute() {
            System.out.println("Task executed without deprecated APIs");
        }
    }

    public static abstract class TaskUsingDeprecatedClass extends FakeDeprecatedGradleClass {
        @TaskAction
        public void execute() {
            System.out.println("Task executed without deprecated APIs");
        }
    }
}
