package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.internalGradleClass;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;

/**
 * Rules to prevent usage of internal Gradle APIs.
 * <p>
 * Internal Gradle APIs are not part of the public API contract and may change
 * or be removed without notice between Gradle versions.
 */
@NullMarked
public class GradleInternalApiRule {

    /**
     * Prevents plugins from using internal Gradle APIs.
     * <p>
     * Internal Gradle APIs (packages containing {@code .internal.}) are not stable
     * and may change or be removed between versions without notice. Use only public
     * Gradle APIs to ensure compatibility across Gradle versions.
     */
    public static final ArchRule PLUGIN_INTERNAL = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().dependOnClassesThat(internalGradleClass)
            .orShould().accessTargetWhere(targetOwner(internalGradleClass))
            .allowEmptyShould(true)
            .because(
                    "Plugins should not use internal Gradle APIs (packages containing '.internal.'). " +
                    "Internal APIs are not stable and may change or be removed without notice. " +
                    "Use only public Gradle APIs documented at https://docs.gradle.org/current/javadoc/"
            );

    /**
     * Prevents tasks from using internal Gradle APIs.
     * <p>
     * Internal Gradle APIs (packages containing {@code .internal.}) are not stable
     * and may change or be removed between versions without notice. Use only public
     * Gradle APIs to ensure compatibility across Gradle versions.
     */
    public static final ArchRule TASK_INTERNAL = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().areAssignableTo("org.gradle.api.Task")
            .and().areNotInterfaces()
            .should().dependOnClassesThat(internalGradleClass)
            .orShould().accessTargetWhere(targetOwner(internalGradleClass))
            .allowEmptyShould(true)
            .because(
                    "Tasks should not use internal Gradle APIs (packages containing '.internal.'). " +
                    "Internal APIs are not stable and may change or be removed without notice. " +
                    "Use only public Gradle APIs documented at https://docs.gradle.org/current/javadoc/"
            );
}
