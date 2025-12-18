package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleTaskContainerApiRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleTaskContainerApiRuleTest.class);

    @Test
    public void pluginUsingGetByName_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskContainerApiRule.USE_NAMED_INSTEAD_OF_GET_BY_NAME,
                PluginUsingGetByName.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("calls getByName");
        assertThat(result.getFailureReport().toString()).contains("use tasks.named()");
    }

    @Test
    public void pluginUsingNamed_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskContainerApiRule.USE_NAMED_INSTEAD_OF_GET_BY_NAME,
                PluginUsingNamed.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void pluginUsingAll_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskContainerApiRule.USE_CONFIGURE_EACH_INSTEAD_OF_ALL,
                PluginUsingAll.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("calls all");
        assertThat(result.getFailureReport().toString()).contains("configureEach()");
    }

    @Test
    public void pluginUsingConfigureEach_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskContainerApiRule.USE_CONFIGURE_EACH_INSTEAD_OF_ALL,
                PluginUsingConfigureEach.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class PluginUsingGetByName implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            Task task = project.getTasks().getByName("build");
            task.setDescription("Custom description");
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingNamed implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            TaskProvider<Task> taskProvider = project.getTasks().named("build");
            taskProvider.configure(task -> {
                task.setDescription("Custom description");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingAll implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().withType(Task.class).all(task -> {
                task.setDescription("Configure all tasks");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingConfigureEach implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().withType(Task.class).configureEach(task -> {
                task.setDescription("Configure each task lazily");
            });
        }
    }
}
