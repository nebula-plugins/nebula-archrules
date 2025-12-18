package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.annotatedWithAny;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.containAnyFieldsInClassHierarchyThat;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.containAnyMethodsInClassHierarchyThat;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.haveTaskAction;
import static com.tngtech.archunit.lang.ArchCondition.from;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

/**
 * Rules to ensure Gradle tasks properly declare their inputs and outputs.
 * <p>
 * Tasks must declare inputs and outputs for incremental builds and caching to work correctly.
 */
@NullMarked
public class GradleTaskInputOutputRule {

    private static final String ANNOTATION_INPUT = "org.gradle.api.tasks.Input";
    private static final String ANNOTATION_INPUT_FILE = "org.gradle.api.tasks.InputFile";
    private static final String ANNOTATION_INPUT_FILES = "org.gradle.api.tasks.InputFiles";
    private static final String ANNOTATION_INPUT_DIRECTORY = "org.gradle.api.tasks.InputDirectory";
    private static final String ANNOTATION_OUTPUT_FILE = "org.gradle.api.tasks.OutputFile";
    private static final String ANNOTATION_OUTPUT_FILES = "org.gradle.api.tasks.OutputFiles";
    private static final String ANNOTATION_OUTPUT_DIRECTORY = "org.gradle.api.tasks.OutputDirectory";
    private static final String ANNOTATION_OUTPUT_DIRECTORIES = "org.gradle.api.tasks.OutputDirectories";
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

    private static final DescribedPredicate<CanBeAnnotated> annotatedWithInputOutputAnnotations =
            annotatedWithAny(INPUT_OUTPUT_ANNOTATIONS)
                    .as("annotated with Input and/or Output annotations");

    /**
     * Ensures that task classes declare at least one input or output.
     * <p>
     * Tasks without declared inputs/outputs cannot participate in incremental builds
     * or build caching, which significantly impacts build performance.
     */
    public static final ArchRule INPUTS_OUTPUTS = ArchRuleDefinition.priority(Priority.HIGH)
            .classes()
            .that().areAssignableTo("org.gradle.api.DefaultTask")
            .and().areNotInterfaces()
            .and(haveTaskAction)
            .and().doNotHaveSimpleName("DefaultTask")
            .should(from(containAnyMethodsInClassHierarchyThat(are(annotatedWithInputOutputAnnotations))))
            .orShould(from(containAnyFieldsInClassHierarchyThat(are(annotatedWithInputOutputAnnotations))))
            .allowEmptyShould(true)
            .because(
                    "Tasks must declare inputs and outputs using @Input, @InputFile, @InputDirectory, " +
                    "@Output, @OutputFile, or @OutputDirectory annotations. " +
                    "This is required for incremental builds and caching to work correctly. " +
                    "See https://docs.gradle.org/current/userguide/incremental_build.html"
            );
}
