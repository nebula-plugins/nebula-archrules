package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradlePluginProjectReferenceRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradlePluginProjectReferenceRuleTest.class);

    @Test
    public void pluginStoringProjectReference_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginProjectReferenceRule.PLUGINS_SHOULD_NOT_STORE_PROJECT_REFERENCES,
                PluginStoringProjectReference.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("PluginStoringProjectReference.project");
        assertThat(result.getFailureReport().toString()).contains("has raw type assignable to org.gradle.api.Project");
        assertThat(result.getFailureReport().toString()).contains("breaks configuration cache");
    }

    @Test
    public void pluginNotStoringProjectReference_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginProjectReferenceRule.PLUGINS_SHOULD_NOT_STORE_PROJECT_REFERENCES,
                PluginNotStoringProjectReference.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void pluginStoringExtractedValues_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginProjectReferenceRule.PLUGINS_SHOULD_NOT_STORE_PROJECT_REFERENCES,
                PluginStoringExtractedValues.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class PluginStoringProjectReference implements Plugin<Project> {
        private Project project;

        @Override
        public void apply(Project project) {
            this.project = project;
            project.getTasks().register("myTask");
        }
    }

    @SuppressWarnings("unused")
    public static class PluginNotStoringProjectReference implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask", task -> {
                task.setDescription("My task");
            });
        }
    }

    @SuppressWarnings("unused")
    public static class PluginStoringExtractedValues implements Plugin<Project> {
        private String projectName;
        private String projectVersion;

        @Override
        public void apply(Project project) {
            this.projectName = project.getName();
            this.projectVersion = project.getVersion().toString();

            project.getTasks().register("myTask", task -> {
                task.setDescription("Task for " + projectName + " version " + projectVersion);
            });
        }
    }
}
