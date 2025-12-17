package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GradleTaskCacheabilityRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradleTaskCacheabilityRuleTest.class);

    @Test
    public void cacheableTaskWithoutPathSensitive_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskCacheabilityRule.cacheableTasksShouldDeclarePathSensitivity,
                CacheableTaskWithoutPathSensitive.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("missing @PathSensitive");
    }

    @Test
    public void cacheableTaskWithPathSensitive_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskCacheabilityRule.cacheableTasksShouldDeclarePathSensitivity,
                CacheableTaskWithPathSensitive.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void cacheableTaskWithInputFileMethodMissingPathSensitive_should_fail() {
        final EvaluationResult result = Runner.check(
                GradleTaskCacheabilityRule.cacheableTasksShouldDeclarePathSensitivity,
                CacheableTaskWithInputFileMethodMissingPathSensitive.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("missing @PathSensitive");
    }

    @Test
    public void cacheableTaskWithInputFileMethodWithPathSensitive_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskCacheabilityRule.cacheableTasksShouldDeclarePathSensitivity,
                CacheableTaskWithInputFileMethodWithPathSensitive.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void cacheableTaskWithOnlyOutputs_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskCacheabilityRule.cacheableTasksShouldDeclarePathSensitivity,
                CacheableTaskWithOnlyOutputs.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void nonCacheableTaskWithoutPathSensitive_should_pass() {
        final EvaluationResult result = Runner.check(
                GradleTaskCacheabilityRule.cacheableTasksShouldDeclarePathSensitivity,
                NonCacheableTaskWithoutPathSensitive.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    @CacheableTask
    public static abstract class CacheableTaskWithoutPathSensitive extends DefaultTask {
        @InputFile
        public File inputFile;

        @OutputFile
        public File outputFile;

        @TaskAction
        public void execute() {
            System.out.println("Processing");
        }
    }

    @SuppressWarnings("unused")
    @CacheableTask
    public static abstract class CacheableTaskWithPathSensitive extends DefaultTask {
        @InputFile
        @PathSensitive(PathSensitivity.RELATIVE)
        public File inputFile;

        @OutputFile
        public File outputFile;

        @TaskAction
        public void execute() {
            System.out.println("Processing");
        }
    }

    @SuppressWarnings("unused")
    @CacheableTask
    public static abstract class CacheableTaskWithInputFileMethodMissingPathSensitive extends DefaultTask {
        @InputFile
        public abstract RegularFileProperty getInputFile();

        @TaskAction
        public void execute() {
            System.out.println("Processing");
        }
    }

    @SuppressWarnings("unused")
    @CacheableTask
    public static abstract class CacheableTaskWithInputFileMethodWithPathSensitive extends DefaultTask {
        @InputFile
        @PathSensitive(PathSensitivity.RELATIVE)
        public abstract RegularFileProperty getInputFile();

        @TaskAction
        public void execute() {
            System.out.println("Processing");
        }
    }

    @SuppressWarnings("unused")
    @CacheableTask
    public static abstract class CacheableTaskWithOnlyOutputs extends DefaultTask {
        @OutputFile
        public abstract RegularFileProperty getOutputFile();

        @TaskAction
        public void execute() {
            System.out.println("Generating output");
        }
    }

    @SuppressWarnings("unused")
    public static abstract class NonCacheableTaskWithoutPathSensitive extends DefaultTask {
        @InputFile
        public File inputFile;

        @TaskAction
        public void execute() {
            System.out.println("Processing");
        }
    }
}
