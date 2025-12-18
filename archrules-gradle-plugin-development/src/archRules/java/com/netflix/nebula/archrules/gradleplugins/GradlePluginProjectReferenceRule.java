package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.GRADLE_PLUGIN;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.GRADLE_PROJECT;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;

/**
 * Rules to ensure Gradle plugins don't store Project references as fields.
 * <p>
 * Storing {@code Project} references in plugin fields breaks configuration cache,
 * prevents garbage collection, and violates Gradle best practices. The {@code Project}
 * should only be accessed during the {@code apply()} method execution.
 */
@NullMarked
class GradlePluginProjectReferenceRule {

    /**
     * Prevents Plugin implementations from storing Project references as fields.
     * Instead, extract needed values during {@code apply()} and store them,
     * or use service injection for factories and services.
     */
    public static final ArchRule PLUGINS_SHOULD_NOT_STORE_PROJECT_REFERENCES = ArchRuleDefinition.priority(Priority.HIGH)
            .fields()
            .that().areDeclaredInClassesThat().implement(GRADLE_PLUGIN)
            .should().notHaveRawType(assignableTo(GRADLE_PROJECT))
            .allowEmptyShould(true)
            .because(
                    "Plugins should not store Project references as fields. " +
                    "This breaks configuration cache and prevents garbage collection. " +
                    "Extract needed values in apply() method or use service injection instead. " +
                    "See https://docs.gradle.org/current/userguide/configuration_cache.html"
            );
}
