package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleTaskRulesTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleTaskRulesTest.class);

    @Test
    public void taskWithPlainFileOutputGetter_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskRules.PROVIDER_PROPERTIES,
                TaskWithPlainFileOutputGetter.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("Use RegularFileProperty");
    }

    @Test
    public void taskWithPropertyApiInput_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskRules.PROVIDER_PROPERTIES,
                TaskWithPropertyApiInput.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithRegularFileProperty_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskRules.PROVIDER_PROPERTIES,
                TaskWithRegularFileProperty.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithDirectoryProperty_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskRules.PROVIDER_PROPERTIES,
                TaskWithDirectoryProperty.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void taskWithoutAnnotations_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskRules.PROVIDER_PROPERTIES,
                TaskWithoutAnnotations.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
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
