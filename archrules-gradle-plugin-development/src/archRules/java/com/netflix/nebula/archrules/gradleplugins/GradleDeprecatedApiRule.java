package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.accessDeprecatedGradleApi;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.deprecatedGradleClass;
import static com.tngtech.archunit.lang.conditions.ArchConditions.accessTargetWhere;

/**
 * Rules to prevent usage of deprecated Gradle APIs.
 * <p>
 * Using deprecated Gradle APIs will cause build failures in future Gradle versions.
 */
@NullMarked
public class GradleDeprecatedApiRule {

    /**
     * Prevents plugins from using deprecated Gradle APIs.
     * <p>
     * Deprecated Gradle APIs will be removed in future versions, causing build failures.
     * Replace deprecated APIs with their modern equivalents as documented in Gradle's
     * upgrade guides.
     */
    public static final ArchRule pluginsShouldNotUseDeprecatedGradleApis = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().dependOnClassesThat(deprecatedGradleClass)
            .orShould(accessTargetWhere(accessDeprecatedGradleApi))
            .allowEmptyShould(true)
            .because(
                    "Plugins should not use deprecated Gradle APIs as they will be removed in future versions. " +
                    "Consult Gradle upgrade guides for modern alternatives. " +
                    "See https://docs.gradle.org/current/userguide/upgrading_version_8.html"
            );

    /**
     * Prevents tasks from using deprecated Gradle APIs.
     * <p>
     * Deprecated Gradle APIs will be removed in future versions, causing build failures.
     * Replace deprecated APIs with their modern equivalents as documented in Gradle's
     * upgrade guides.
     */
    public static final ArchRule tasksShouldNotUseDeprecatedGradleApis = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().areAssignableTo("org.gradle.api.Task")
            .and().areNotInterfaces()
            .should().dependOnClassesThat(deprecatedGradleClass)
            .orShould(accessTargetWhere(accessDeprecatedGradleApi))
            .allowEmptyShould(true)
            .because(
                    "Tasks should not use deprecated Gradle APIs as they will be removed in future versions. " +
                    "Consult Gradle upgrade guides for modern alternatives. " +
                    "See https://docs.gradle.org/current/userguide/upgrading_version_8.html"
            );
}
