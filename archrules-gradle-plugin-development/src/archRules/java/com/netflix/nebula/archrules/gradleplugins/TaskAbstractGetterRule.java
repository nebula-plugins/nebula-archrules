package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.conditions.ArchPredicates;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.common.JavaMethod.Predicates.aGetter;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.aGradleTaskClass;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.hasRichPropertyReturnType;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.JavaModifier.PRIVATE;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

@NullMarked
public class TaskAbstractGetterRule {

    private static final DescribedPredicate<JavaMethod> richTaskPropertyGetters = ArchPredicates.are(aGetter())
            .and(are(hasRichPropertyReturnType))
            .and(not(modifier(PRIVATE)))
            .and(not(annotatedWith("javax.inject.Inject")))
            .and(not(annotatedWith("org.gradle.api.tasks.options.OptionValues")))
            .and(are(declaredIn(aGradleTaskClass())))
            .and(not(declaredIn("org.gradle.api.Task")))
            .and(not(declaredIn("org.gradle.api.DefaultTask")))
            .and(not(declaredIn("org.gradle.api.internal.AbstractTask")))
            .as("task property getters");

    /**
     * Inspired by
     * {@link <a href="https://github.com/gradle/gradle/blob/master/testing/architecture-test/src/test/java/org/gradle/architecture/test/PropertyUsageArchitectureTest.java">Gradle</a>}
     */
    static final ArchRule RULE = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods().that(are(richTaskPropertyGetters))
            .should().haveModifier(JavaModifier.ABSTRACT)
            .allowEmptyShould(true)
            .because("task implementations should define properties as abstract getters");
}
