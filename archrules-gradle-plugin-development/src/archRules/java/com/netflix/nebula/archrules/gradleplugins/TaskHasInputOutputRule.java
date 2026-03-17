package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.aGradleTaskClass;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.annotatedWithInputOutputAnnotations;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.containAnyFieldsInClassHierarchyThat;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.containAnyMethodsInClassHierarchyThat;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.haveTaskAction;
import static com.tngtech.archunit.lang.ArchCondition.from;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

@NullMarked
public class TaskHasInputOutputRule {
    /**
     * Ensures that task classes declare at least one input or output.
     * <p>
     * Tasks without declared inputs/outputs cannot participate in incremental builds
     * or build caching, which significantly impacts build performance.
     */
    static final ArchRule RULE = ArchRuleDefinition.priority(Priority.HIGH)
            .classes()
            .that(are(aGradleTaskClass()))
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
