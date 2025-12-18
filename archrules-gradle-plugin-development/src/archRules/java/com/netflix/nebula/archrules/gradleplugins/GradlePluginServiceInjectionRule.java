package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.callsMethodOn;

/**
 * Rules to ensure Gradle plugins use service injection instead of accessing services from Project.
 * <p>
 * Modern Gradle plugin development should use constructor injection for services like
 * {@code ObjectFactory} and {@code ProviderFactory} instead of obtaining them from the
 * {@code Project} instance.
 */
@NullMarked
class GradlePluginServiceInjectionRule {

    private static final DescribedPredicate<JavaAccess<?>> callsGetObjects =
            callsMethodOn("getObjects", "org.gradle.api.Project");

    private static final DescribedPredicate<JavaAccess<?>> callsGetProviders =
            callsMethodOn("getProviders", "org.gradle.api.Project");

    /**
     * Prevents plugins from calling {@code project.getObjects()}.
     * <p>
     * Instead of calling {@code project.getObjects()}, plugins should inject
     * {@code ObjectFactory} via constructor with {@code @Inject} annotation.
     * This improves testability and follows Gradle's service injection pattern.
     */
    public static final ArchRule USE_INJECTED_OBJECT_FACTORY = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().callMethodWhere(callsGetObjects)
            .allowEmptyShould(true)
            .because(
                    "Plugins should inject ObjectFactory via constructor instead of calling project.getObjects(). " +
                    "Use @Inject constructor parameter for better testability and to follow Gradle best practices. " +
                    "Example: @Inject public MyPlugin(ObjectFactory objects) { this.objects = objects; } " +
                    "See https://docs.gradle.org/current/userguide/service_injection.html"
            );

    /**
     * Prevents plugins from calling {@code project.getProviders()}.
     * <p>
     * Instead of calling {@code project.getProviders()}, plugins should inject
     * {@code ProviderFactory} via constructor with {@code @Inject} annotation.
     * This improves testability and follows Gradle's service injection pattern.
     */
    public static final ArchRule USE_INJECTED_PROVIDER_FACTORY = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().callMethodWhere(callsGetProviders)
            .allowEmptyShould(true)
            .because(
                    "Plugins should inject ProviderFactory via constructor instead of calling project.getProviders(). " +
                    "Use @Inject constructor parameter for better testability and to follow Gradle best practices. " +
                    "Example: @Inject public MyPlugin(ProviderFactory providers) { this.providers = providers; } " +
                    "See https://docs.gradle.org/current/userguide/service_injection.html"
            );
}
