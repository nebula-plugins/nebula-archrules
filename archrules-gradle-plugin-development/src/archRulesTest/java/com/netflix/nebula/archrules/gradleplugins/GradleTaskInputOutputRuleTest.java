package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleTaskInputOutputRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleTaskInputOutputRuleTest.class);

    @Test
    public void taskWithoutInputOutput_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskInputOutputRule.tasksShouldDeclareInputsOrOutputs,
                TaskWithoutInputOutput.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("no declared inputs or outputs");
    }

    @Test
    public void taskWithInputAnnotation_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskInputOutputRule.tasksShouldDeclareInputsOrOutputs,
                TaskWithInputAnnotation.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithInputFileAnnotation_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskInputOutputRule.tasksShouldDeclareInputsOrOutputs,
                TaskWithInputFileAnnotation.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithOutputAnnotation_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskInputOutputRule.tasksShouldDeclareInputsOrOutputs,
                TaskWithOutputAnnotation.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithoutTaskAction_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskInputOutputRule.tasksShouldDeclareInputsOrOutputs,
                TaskWithoutTaskAction.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithoutInputOutput extends DefaultTask {
        @TaskAction
        public void execute() {
            System.out.println("Task executed without inputs/outputs");
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithInputAnnotation extends DefaultTask {
        @Input
        public String message;

        @TaskAction
        public void execute() {
            System.out.println(message);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithInputFileAnnotation extends DefaultTask {
        @InputFile
        public File inputFile;

        @TaskAction
        public void execute() {
            System.out.println("Processing: " + inputFile);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithOutputAnnotation extends DefaultTask {
        @OutputFile
        public File outputFile;

        @TaskAction
        public void execute() {
            System.out.println("Writing to: " + outputFile);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithoutTaskAction extends DefaultTask {
        public void someMethod() {
            System.out.println("Not a task action");
        }
    }
}
