package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleDeprecatedApiRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleDeprecatedApiRuleTest.class);

    @Test
    public void pluginNotUsingDeprecatedApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleDeprecatedApiRule.pluginsShouldNotUseDeprecatedGradleApis,
                PluginNotUsingDeprecatedApis.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskNotUsingDeprecatedApis_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleDeprecatedApiRule.tasksShouldNotUseDeprecatedGradleApis,
                TaskNotUsingDeprecatedApis.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class PluginNotUsingDeprecatedApis implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask", task -> {
                task.setGroup("custom");
                task.setDescription("My custom task");
            });
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskNotUsingDeprecatedApis extends DefaultTask {
        @TaskAction
        public void execute() {
            System.out.println("Task executed without deprecated APIs");
        }
    }
}
