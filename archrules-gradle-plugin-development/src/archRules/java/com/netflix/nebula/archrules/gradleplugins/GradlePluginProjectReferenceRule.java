package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

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
            .classes()
            .that().implement("org.gradle.api.Plugin")
            .should(notHaveProjectFields())
            .allowEmptyShould(true)
            .because(
                    "Plugins should not store Project references as fields. " +
                    "This breaks configuration cache and prevents garbage collection. " +
                    "Extract needed values in apply() method or use service injection instead. " +
                    "See https://docs.gradle.org/current/userguide/configuration_cache.html"
            );

    private static ArchCondition<JavaClass> notHaveProjectFields() {
        return new ArchCondition<JavaClass>("not have Project fields") {
            @Override
            public void check(JavaClass pluginClass, ConditionEvents events) {
                for (JavaField field : pluginClass.getAllFields()) {
                    if (isProjectType(field.getRawType())) {
                        String message = String.format(
                                "Plugin %s has field '%s' of type %s. " +
                                "Storing Project references breaks configuration cache. " +
                                "Extract needed values in apply() or use service injection.",
                                pluginClass.getSimpleName(),
                                field.getName(),
                                field.getRawType().getSimpleName()
                        );
                        events.add(SimpleConditionEvent.violated(field, message));
                    }
                }
            }

            private boolean isProjectType(JavaClass type) {
                return type.isAssignableTo("org.gradle.api.Project");
            }
        };
    }
}
