package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class TaskInputOutputFieldRuleTest {
    @Test
    public void test_input() {
        final EvaluationResult result = Runner.check(
                TaskInputOutputFieldRule.RULE,
                TaskWithInputField.class
        );
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("no fields that are declared in classes that are a gradle task")
                .contains("should be annotated with Input and/or Output annotations, ")
                .contains("because Task input/output properties should be declared as abstract getter methods");
    }

    @Test
    public void test_input_file() {
        final EvaluationResult result = Runner.check(
                TaskInputOutputFieldRule.RULE,
                TaskWithFileInputField.class
        );
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("no fields that are declared in classes that are a gradle task")
                .contains("should be annotated with Input and/or Output annotations, ")
                .contains("because Task input/output properties should be declared as abstract getter methods");
    }

    static abstract class TaskWithInputField extends DefaultTask {
        @Input
        public String message;

        @TaskAction
        public void execute() {
            System.out.println(message);
        }
    }

    public static abstract class TaskWithFileInputField extends DefaultTask {
        @InputFile
        public File inputFile;

        @TaskAction
        public void execute() {
            System.out.println("Processing: " + inputFile);
        }
    }
}
