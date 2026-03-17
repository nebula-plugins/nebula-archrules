package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.common.CanBeAnnotated.Predicates.annotatedWithAny;
import static com.netflix.nebula.archrules.common.JavaMethod.Predicates.aGetter;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.aGradleTaskClass;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

@NullMarked
public class TaskRegularFilePropertyRule {

    static final ArchRule RULE = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods()
            .that().areDeclaredInClassesThat(are(aGradleTaskClass()))
            .and(are(aGetter()))
            .and(are(annotatedWithAny("org.gradle.api.tasks.InputFile", "org.gradle.api.tasks.OutputFile")))
            .should().haveRawReturnType("org.gradle.api.file.RegularFileProperty")
            .allowEmptyShould(true)
            .because("Single file inputs and outputs should use RegularFileProperty " +
                     "for better API cohesion and to prevent misuse");

}
