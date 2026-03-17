package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class TaskRegularFilePropertyRuleTest {

    @Test
    public void test_nonregular_file() {
        final EvaluationResult result = Runner.check(
                TaskRegularFilePropertyRule.RULE,
                TaskWithFileProperty.class
        );
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("methods that are declared in classes that are a gradle task")
                .contains("and are a getter")
                .contains("and are annotated with any [org.gradle.api.tasks.InputFile, org.gradle.api.tasks.OutputFile]")
                .contains("should have raw return type org.gradle.api.file.RegularFileProperty")
                .contains("because Single file inputs and outputs should use RegularFileProperty")
                .contains("for better API cohesion and to prevent misuse");
    }

    static abstract class TaskWithFileProperty extends DefaultTask {
        @OutputFile
        public abstract Property<File> getOutputFile();

        @TaskAction
        public void execute() {
            System.out.println("Writing to: " + getOutputFile().get());
        }
    }

}
