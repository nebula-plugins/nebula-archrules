package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleTaskActionRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleTaskActionRuleTest.class);

    @Test
    public void taskActionAccessingProject_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskActionRule.taskActionShouldNotAccessProject,
                TaskAccessingProject.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void taskActionCallingGetProject_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskActionRule.taskActionShouldNotAccessProject,
                TaskCallingGetProject.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void taskActionNotAccessingProject_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskActionRule.taskActionShouldNotAccessProject,
                TaskNotAccessingProject.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskActionAccessingProjectInConstructor_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskActionRule.taskActionShouldNotAccessProject,
                TaskAccessingProjectInConstructor.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskActionCallingGetTaskDependencies_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskActionRule.taskActionShouldNotCallGetTaskDependencies,
                TaskCallingGetTaskDependencies.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void taskActionNotCallingGetTaskDependencies_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskActionRule.taskActionShouldNotCallGetTaskDependencies,
                TaskNotCallingGetTaskDependencies.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static abstract class TaskAccessingProject extends DefaultTask {
        @TaskAction
        public void execute() {
            Project project = getProject();
            String version = project.getVersion().toString();
            System.out.println("Version: " + version);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskCallingGetProject extends DefaultTask {
        @TaskAction
        public void execute() {
            getProject();
            System.out.println("Task executed");
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskNotAccessingProject extends DefaultTask {
        @Input
        public abstract Property<String> getVersion();

        @TaskAction
        public void execute() {
            String version = getVersion().get();
            System.out.println("Version: " + version);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskAccessingProjectInConstructor extends DefaultTask {
        @Input
        public abstract Property<String> getVersion();

        public TaskAccessingProjectInConstructor() {
            getVersion().set(getProject().getVersion().toString());
        }

        @TaskAction
        public void execute() {
            String version = getVersion().get();
            System.out.println("Version: " + version);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskCallingGetTaskDependencies extends DefaultTask {
        @TaskAction
        public void execute() {
            getTaskDependencies();
            System.out.println("Task executed");
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskNotCallingGetTaskDependencies extends DefaultTask {
        @TaskAction
        public void execute() {
            System.out.println("Task executed");
        }
    }
}
