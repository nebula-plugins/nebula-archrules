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

import static com.netflix.nebula.archrules.common.JavaMethod.Predicates.aGetter;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.fieldWithTypeIn;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.hasRichPropertyReturnType;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.isProviderApiType;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.pluginExtensionClass;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.EXTENSION_TYPES_REQUIRING_PROVIDER;
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

    /**
     * Detects plugin extension fields with plain types that should use Provider API.
     * <p>
     * Extension fields should use Provider API types for lazy configuration.
     * Only checks non-static fields in extension classes.
     */
    public static final ArchRule EXTENSION_FIELDS_USE_PROVIDER_API = ArchRuleDefinition.priority(Priority.MEDIUM)
            .fields()
            .that().areDeclaredInClassesThat(are(pluginExtensionClass))
            .and().areNotStatic()
            .and(haveTypeThatShouldUseProvider())
            .should(useProviderApiType())
            .allowEmptyShould(true)
            .because(
                    "Plugin extension fields should use Provider API types (Property<T>, ListProperty<T>, " +
                    "SetProperty<T>) instead of plain mutable types. " +
                    "This enables lazy configuration and better integration with Gradle's configuration system. " +
                    "See https://docs.gradle.org/current/userguide/lazy_configuration.html"
            );

    private static boolean shouldUseProviderApi(JavaClass type) {
        return EXTENSION_TYPES_REQUIRING_PROVIDER.contains(type.getName()) && !isProviderApiType(type);
    }

    private static DescribedPredicate<JavaField> haveTypeThatShouldUseProvider() {
        return fieldWithTypeIn(EXTENSION_TYPES_REQUIRING_PROVIDER)
                .and(new DescribedPredicate<JavaField>("not Provider API type") {
                    @Override
                    public boolean test(JavaField field) {
                        return !isProviderApiType(field.getRawType());
                    }
                })
                .as("have type that should use Provider API");
    }

    private static ArchCondition<JavaField> useProviderApiType() {
        return new ArchCondition<JavaField>("use Provider API type") {
            @Override
            public void check(JavaField field, ConditionEvents events) {
                String message = String.format(
                        "Field <%s.%s> has type %s. Use Property<%s> for lazy configuration.",
                        field.getOwner().getName(),
                        field.getName(),
                        field.getRawType().getSimpleName(),
                        field.getRawType().getSimpleName()
                );
                events.add(SimpleConditionEvent.violated(field, message));
            }
        };
    }

    static final DescribedPredicate<JavaMethod> richExtensionPropertyGetters = ArchPredicates.are(aGetter())
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
