package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradlePluginLazyTaskRegistrationRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradlePluginLazyTaskRegistrationRuleTest.class);

    @Test
    public void pluginUsingEagerTaskCreation_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginLazyTaskRegistrationRule.LAZY_TASK_CREATION,
                PluginUsingEagerTaskCreation.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("where task is created eagerly");
        assertThat(result.getFailureReport().toString()).contains("use tasks.register()");
        assertThat(result.getFailureReport().toString())
                .as("wrong call is shown")
                .contains("calls method <org.gradle.api.Project.task(java.lang.String, org.gradle.api.Action)>");
    }

    @Test
    public void pluginUsingTasksCreate_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginLazyTaskRegistrationRule.LAZY_TASK_CREATION,
                PluginUsingTasksCreate.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("where task is created eagerly");
    }

    @Test
    public void pluginUsingLazyTaskRegistration_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginLazyTaskRegistrationRule.LAZY_TASK_CREATION,
                PluginUsingLazyTaskRegistration.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void pluginUsingTasksNamed_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginLazyTaskRegistrationRule.LAZY_TASK_CREATION,
                PluginUsingTasksNamed.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class PluginUsingEagerTaskCreation implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.task("myTask", task -> {
                task.setGroup("custom");
                task.setDescription("My custom task");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingTasksCreate implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().create("myTask", Task.class, task -> {
                task.setGroup("custom");
                task.setDescription("My custom task");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingLazyTaskRegistration implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            // Good practice: lazy task registration
            project.getTasks().register("myTask", Task.class, task -> {
                task.setGroup("custom");
                task.setDescription("My custom task");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingTasksNamed implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask");

            project.getTasks().named("myTask", task -> {
                task.setGroup("custom");
            });
        }
    }
}
