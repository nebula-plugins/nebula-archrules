package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Rules to ensure Gradle tasks properly declare their inputs and outputs.
 * <p>
 * Tasks must declare inputs and outputs for incremental builds and caching to work correctly.
 */
@NullMarked
public class GradleTaskInputOutputRule implements ArchRulesService {

    private static final String ANNOTATION_INPUT = "org.gradle.api.tasks.Input";
    private static final String ANNOTATION_INPUT_FILE = "org.gradle.api.tasks.InputFile";
    private static final String ANNOTATION_INPUT_FILES = "org.gradle.api.tasks.InputFiles";
    private static final String ANNOTATION_INPUT_DIRECTORY = "org.gradle.api.tasks.InputDirectory";
    private static final String ANNOTATION_OUTPUT_FILE = "org.gradle.api.tasks.OutputFile";
    private static final String ANNOTATION_OUTPUT_FILES = "org.gradle.api.tasks.OutputFiles";
    private static final String ANNOTATION_OUTPUT_DIRECTORY = "org.gradle.api.tasks.OutputDirectory";
    private static final String ANNOTATION_OUTPUT_DIRECTORIES = "org.gradle.api.tasks.OutputDirectories";
    private static final String ANNOTATION_TASK_ACTION = "org.gradle.api.tasks.TaskAction";

    private static class LazyHolder {
        private static final Set<String> INPUT_OUTPUT_ANNOTATIONS = new HashSet<>(Arrays.asList(
                ANNOTATION_INPUT,
                ANNOTATION_INPUT_FILE,
                ANNOTATION_INPUT_FILES,
                ANNOTATION_INPUT_DIRECTORY,
                ANNOTATION_OUTPUT_FILE,
                ANNOTATION_OUTPUT_FILES,
                ANNOTATION_OUTPUT_DIRECTORY,
                ANNOTATION_OUTPUT_DIRECTORIES
        ));
    }

    private static Set<String> getInputOutputAnnotations() {
        return LazyHolder.INPUT_OUTPUT_ANNOTATIONS;
    }

    /**
     * Ensures that task classes declare at least one input or output.
     * <p>
     * Tasks without declared inputs/outputs cannot participate in incremental builds
     * or build caching, which significantly impacts build performance.
     */
    public static final ArchRule tasksShouldDeclareInputsOrOutputs = ArchRuleDefinition.priority(Priority.HIGH)
            .classes()
            .that().areAssignableTo("org.gradle.api.DefaultTask")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("DefaultTask")
            .should(declareInputsOrOutputs())
            .allowEmptyShould(true)
            .because(
                    "Tasks must declare inputs and outputs using @Input, @InputFile, @InputDirectory, " +
                    "@Output, @OutputFile, or @OutputDirectory annotations. " +
                    "This is required for incremental builds and caching to work correctly. " +
                    "See https://docs.gradle.org/current/userguide/incremental_build.html"
            );

    private static ArchCondition<JavaClass> declareInputsOrOutputs() {
        return new ArchCondition<JavaClass>("declare at least one input or output") {
            @Override
            public void check(JavaClass taskClass, ConditionEvents events) {
                if (!hasTaskAction(taskClass)) {
                    return;
                }

                boolean hasInputOrOutput = hasInputOutputAnnotation(taskClass);

                if (!hasInputOrOutput) {
                    String message = String.format(
                            "Task %s has @TaskAction method(s) but no declared inputs or outputs. " +
                            "Add @Input, @InputFile, @InputDirectory, @Output, @OutputFile, or @OutputDirectory " +
                            "annotations to enable incremental builds and caching.",
                            taskClass.getSimpleName()
                    );
                    events.add(SimpleConditionEvent.violated(taskClass, message));
                }
            }

            private boolean hasTaskAction(JavaClass taskClass) {
                return taskClass.getAllMethods().stream()
                        .anyMatch(method -> method.isAnnotatedWith(ANNOTATION_TASK_ACTION));
            }

            private boolean hasInputOutputAnnotation(JavaClass taskClass) {
                for (JavaField field : taskClass.getAllFields()) {
                    if (hasAnyInputOutputAnnotation(field)) {
                        return true;
                    }
                }

                for (JavaMethod method : taskClass.getAllMethods()) {
                    if (hasAnyInputOutputAnnotation(method)) {
                        return true;
                    }
                }

                return false;
            }

            private boolean hasAnyInputOutputAnnotation(JavaField field) {
                return getInputOutputAnnotations().stream()
                        .anyMatch(field::isAnnotatedWith);
            }

            private boolean hasAnyInputOutputAnnotation(JavaMethod method) {
                return getInputOutputAnnotations().stream()
                        .anyMatch(method::isAnnotatedWith);
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("gradle-task-input-output-declaration", tasksShouldDeclareInputsOrOutputs);
        return rules;
    }
}
