package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.service.ServiceRegistry;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleInternalApiRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleInternalApiRuleTest.class);

    @Test
    public void pluginNotUsingInternalApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleInternalApiRule.pluginsShouldNotUseInternalGradleApis,
                PluginNotUsingInternalApis.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskNotUsingInternalApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleInternalApiRule.tasksShouldNotUseInternalGradleApis,
                TaskNotUsingInternalApis.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void pluginUsingPublicGradleApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleInternalApiRule.pluginsShouldNotUseInternalGradleApis,
                PluginUsingPublicGradleApis.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void pluginUsingInternalApi_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleInternalApiRule.pluginsShouldNotUseInternalGradleApis,
                PluginUsingInternalApi.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("internal Gradle API");
        assertThat(result.getFailureReport().toString()).contains(".internal.");
    }

    @Test
    public void taskUsingInternalApi_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleInternalApiRule.tasksShouldNotUseInternalGradleApis,
                TaskUsingInternalApi.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("internal Gradle API");
    }

    @SuppressWarnings("unused")
    public static class PluginNotUsingInternalApis implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask", task -> {
                task.setGroup("custom");
                task.setDescription("My custom task");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingPublicGradleApis implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask");
            project.getExtensions().create("myExtension", MyExtension.class);
            String version = project.getVersion().toString();
            System.out.println("Project version: " + version);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskNotUsingInternalApis extends DefaultTask {
        @TaskAction
        public void execute() {
            String taskName = getName();
            System.out.println("Executing task: " + taskName);
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingInternalApi implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            ProjectInternal projectInternal = (ProjectInternal) project;
            ServiceRegistry services = projectInternal.getServices();
            System.out.println("Using internal API: " + services);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskUsingInternalApi extends DefaultTask {
        @TaskAction
        public void execute() {
            ProjectInternal projectInternal = (ProjectInternal) getProject();
            ServiceRegistry services = projectInternal.getServices();
            System.out.println("Using internal API: " + services);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class MyExtension {
        public abstract Property<String> getValue();
    }
}
