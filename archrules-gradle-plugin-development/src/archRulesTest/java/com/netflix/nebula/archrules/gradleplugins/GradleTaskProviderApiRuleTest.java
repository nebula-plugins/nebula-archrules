package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleTaskProviderApiRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleTaskProviderApiRuleTest.class);

    @Test
    public void taskWithPlainStringInput_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithPlainStringInput.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("Use Property<String>");
    }

    @Test
    public void taskWithPlainFileInputField_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithPlainFileInputField.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("Use RegularFileProperty");
    }

    @Test
    public void taskWithPlainFileOutputGetter_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithPlainFileOutputGetter.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("Use RegularFileProperty");
    }

    @Test
    public void taskWithPropertyApiInput_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithPropertyApiInput.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithRegularFileProperty_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithRegularFileProperty.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithDirectoryProperty_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithDirectoryProperty.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithoutAnnotations_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskProviderApiRule.taskInputOutputPropertiesShouldUseProviderApi,
                TaskWithoutAnnotations.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithPlainStringInput extends DefaultTask {
        @Input
        public String message;

        @TaskAction
        public void execute() {
            System.out.println(message);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithPlainFileInputField extends DefaultTask {
        @InputFile
        public File inputFile;

        @TaskAction
        public void execute() {
            System.out.println("Processing: " + inputFile);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithPlainFileOutputGetter extends DefaultTask {
        private File outputFile;

        @OutputFile
        public File getOutputFile() {
            return outputFile;
        }

        public void setOutputFile(File file) {
            this.outputFile = file;
        }

        @TaskAction
        public void execute() {
            System.out.println("Writing to: " + outputFile);
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithPropertyApiInput extends DefaultTask {
        @Input
        public abstract Property<String> getMessage();

        @TaskAction
        public void execute() {
            System.out.println(getMessage().get());
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithRegularFileProperty extends DefaultTask {
        @InputFile
        public abstract RegularFileProperty getInputFile();

        @TaskAction
        public void execute() {
            System.out.println("Processing: " + getInputFile().get());
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithDirectoryProperty extends DefaultTask {
        @OutputDirectory
        public abstract DirectoryProperty getOutputDir();

        @TaskAction
        public void execute() {
            System.out.println("Writing to: " + getOutputDir().get());
        }
    }

    @SuppressWarnings("unused")
    public static abstract class TaskWithoutAnnotations extends DefaultTask {
        // No input/output annotations, so plain types are fine
        public String message;

        @TaskAction
        public void execute() {
            System.out.println(message);
        }
    }
}
