package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;

/**
 * Rules to ensure Gradle plugin extensions use Provider API for properties.
 * <p>
 * Plugin extension classes should use Provider API types ({@code Property<T>},
 * {@code ListProperty<T>}, etc.) instead of plain types for lazy configuration.
 */
@NullMarked
class GradlePluginNoDslRule {

    /**
     * Detects plugin extension fields with plain types that should use Provider API.
     * <p>
     * Extension fields should use Provider API types for lazy configuration.
     * Only checks non-static fields in extension classes.
     */
    public static final ArchRule DONT_USE_KOTLIN_DSL = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should()
            .dependOnClassesThat(resideInAPackage("org.gradle.kotlin.dsl.."))
            .allowEmptyShould(true)
            .because(
                    "The kotlin-dsl plugin should not be used in binary Gradle plugin projects. " +
                    "Use built-in Gradle APIs instead."
            );

}
