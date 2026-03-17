package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.aGradleTaskClass;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.annotatedWithInputOutputAnnotations;
import static com.tngtech.archunit.lang.conditions.ArchConditions.be;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

@NullMarked
public class TaskInputOutputFieldRule {
    /**
     * Detects task input/output properties that are fields instead of abstract getter methods
     */
    static final ArchRule RULE = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noFields()
            .that().areDeclaredInClassesThat(are(aGradleTaskClass()))
            .should(be(annotatedWithInputOutputAnnotations))
            .allowEmptyShould(true)
            .because("Task input/output properties should be declared as abstract getter methods");
}
