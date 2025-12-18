package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.conditions.ArchPredicates;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.getters;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.hasRichPropertyReturnType;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.isProviderApiType;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.pluginExtensionClass;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_LANG_BOOLEAN;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_LANG_INTEGER;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_LANG_LONG;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_LANG_STRING;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_UTIL_LIST;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_UTIL_SET;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

/**
 * Rules to ensure Gradle plugin extensions use Provider API for properties.
 * <p>
 * Plugin extension classes should use Provider API types ({@code Property<T>},
 * {@code ListProperty<T>}, etc.) instead of plain types for lazy configuration.
 */
@NullMarked
class GradlePluginExtensionProviderApiRule {

    private static final Set<String> TYPES_THAT_SHOULD_USE_PROVIDER = new HashSet<>(Arrays.asList(
            JAVA_LANG_STRING,
            JAVA_LANG_INTEGER,
            JAVA_LANG_LONG,
            JAVA_LANG_BOOLEAN,
            JAVA_UTIL_LIST,
            JAVA_UTIL_SET
    ));

    /**
     * Detects plugin extension classes with mutable properties that should use Provider API.
     * <p>
     * Extension classes used for plugin configuration should use Provider API types
     * for lazy configuration. This enables better integration with Gradle's configuration
     * system and improves build performance.
     * <p>
     * Only checks classes that are both named with "Extension" suffix AND referenced from Plugin code
     * to reduce false positives from naming conventions.
     */
    public static final ArchRule EXTENSION_PROPERTIES_USE_PROVIDER_API = ArchRuleDefinition.priority(Priority.MEDIUM)
            .classes()
            .that(are(pluginExtensionClass))
            .should(useProviderApiForProperties())
            .allowEmptyShould(true)
            .because(
                    "Plugin extension properties should use Provider API types (Property<T>, ListProperty<T>, " +
                    "SetProperty<T>) instead of plain mutable types. " +
                    "This enables lazy configuration and better integration with Gradle's configuration system. " +
                    "See https://docs.gradle.org/current/userguide/lazy_configuration.html"
            );

    private static ArchCondition<JavaClass> useProviderApiForProperties() {
        return new ArchCondition<JavaClass>("use Provider API for properties") {
            @Override
            public void check(JavaClass extensionClass, ConditionEvents events) {
                for (JavaField field : extensionClass.getAllFields()) {
                    if (field.getModifiers().contains(JavaModifier.STATIC)) {
                        continue;
                    }

                    if (shouldUseProviderApi(field.getRawType()) && !isProviderApiType(field.getRawType())) {
                        String message = String.format(
                                "Extension %s has field '%s' of type %s. " +
                                "Use Property<%s> for lazy configuration.",
                                extensionClass.getSimpleName(),
                                field.getName(),
                                field.getRawType().getSimpleName(),
                                field.getRawType().getSimpleName()
                        );
                        events.add(SimpleConditionEvent.violated(field, message));
                    }
                }

                for (JavaMethod method : extensionClass.getMethods()) {
                    if (!getters.test(method)) {
                        continue;
                    }

                    if (shouldUseProviderApi(method.getRawReturnType()) && !isProviderApiType(method.getRawReturnType())) {
                        String message = String.format(
                                "Extension %s has getter '%s()' returning type %s. " +
                                "Use Property<%s> for lazy configuration.",
                                extensionClass.getSimpleName(),
                                method.getName(),
                                method.getRawReturnType().getSimpleName(),
                                method.getRawReturnType().getSimpleName()
                        );
                        events.add(SimpleConditionEvent.violated(method, message));
                    }
                }
            }

            private boolean shouldUseProviderApi(JavaClass type) {
                return TYPES_THAT_SHOULD_USE_PROVIDER.contains(type.getName());
            }
        };
    }

    static final DescribedPredicate<JavaMethod> richExtensionPropertyGetters = ArchPredicates.are(getters)
            .and(are(hasRichPropertyReturnType))
            .and(not(modifier(JavaModifier.PRIVATE)))
            .and(not(annotatedWith("javax.inject.Inject")))
            .and(declaredIn(pluginExtensionClass))
            .as("extension property getters");

    /**
     * Ensures that extension classes define Provider API properties as abstract getters.
     * <p>
     * Extension property getters that return Provider API types should be abstract,
     * allowing Gradle to generate the implementation at runtime. This is the recommended
     * pattern for plugin extensions.
     * <p>
     * Only checks classes that are both named with "Extension" suffix AND referenced from Plugin code
     * to reduce false positives from naming conventions.
     */
    public static final ArchRule EXTENSION_ABSTRACT_GETTERS = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods().that(are(richExtensionPropertyGetters))
            .should().haveModifier(JavaModifier.ABSTRACT)
            .allowEmptyShould(true)
            .because(
                    "Extension property getters returning Provider API types should be abstract. " +
                    "This allows Gradle to generate the implementation at runtime. " +
                    "See https://docs.gradle.org/current/userguide/custom_plugins.html#sec:implementing_an_extension"
            );

}
