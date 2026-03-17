package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
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

import java.util.HashMap;
import java.util.Map;

import static com.netflix.nebula.archrules.common.CanBeAnnotated.Predicates.annotatedWithAny;
import static com.netflix.nebula.archrules.common.JavaMethod.Predicates.aGetter;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.aGradleTaskClass;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.annotatedWithInputOutputAnnotations;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.containAnyFieldsInClassHierarchyThat;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.containAnyMethodsInClassHierarchyThat;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.hasRichPropertyReturnType;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.haveTaskAction;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.isProviderApiType;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.ANNOTATION_INPUT_DIRECTORY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.ANNOTATION_INPUT_FILE;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.ANNOTATION_OUTPUT_DIRECTORY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.ANNOTATION_OUTPUT_FILE;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_IO_FILE;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_UTIL_LIST;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_UTIL_MAP;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.JAVA_UTIL_SET;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.RECOMMENDATION_DIRECTORY_PROPERTY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.RECOMMENDATION_LIST_PROPERTY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.RECOMMENDATION_MAP_PROPERTY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.RECOMMENDATION_REGULAR_FILE_PROPERTY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.RECOMMENDATION_SET_PROPERTY;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.TASK_TYPES_REQUIRING_PROVIDER;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.JavaModifier.PRIVATE;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.ArchCondition.from;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.is;

/**
 * Rules for Gradle task classes to ensure they use the Provider API for lazy configuration.
 * <p>
 * The Provider API ({@code Property<T>}, {@code Provider<T>}, {@code ConfigurableFileCollection})
 * enables lazy configuration and configuration avoidance, which are critical for build performance.
 */
@NullMarked
class GradleTaskRules {

    private static class LazyHolder {
        private static final Map<String, String> TYPE_TO_PROVIDER;

        static {
            Map<String, String> map = new HashMap<>();
            map.put(JAVA_UTIL_LIST, RECOMMENDATION_LIST_PROPERTY);
            map.put(JAVA_UTIL_SET, RECOMMENDATION_SET_PROPERTY);
            map.put(JAVA_UTIL_MAP, RECOMMENDATION_MAP_PROPERTY);
            TYPE_TO_PROVIDER = map;
        }
    }

    private static java.util.Set<String> getMutableTypesThatShouldUseProvider() {
        return TASK_TYPES_REQUIRING_PROVIDER;
    }

    private static Map<String, String> getTypeToProviderMap() {
        return LazyHolder.TYPE_TO_PROVIDER;
    }

    /**
     * Detects task input/output properties that should use Provider API types.
     * <p>
     * Task abstract getter methods annotated with {@code @Input}, {@code @InputFile}, {@code @InputDirectory},
     * {@code @OutputFile}, {@code @OutputDirectory}, etc. should use Provider API types
     * ({@code Property<T>}, {@code RegularFileProperty}, {@code DirectoryProperty}, etc.)
     * instead of plain types for lazy configuration.
     */
    static final ArchRule PROVIDER_PROPERTIES = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods()
            .that().areDeclaredInClassesThat(are(aGradleTaskClass()))
            .and(annotatedWithInputOutputAnnotations)
            .and(is(aGetter()))
            .should(useProviderApiForInputOutputPropertiesMethods())
            .allowEmptyShould(true)
            .because(
                    "Task input/output properties should use Provider API types (Property<T>, RegularFileProperty, " +
                    "DirectoryProperty, ConfigurableFileCollection) instead of plain types. " +
                    "This enables lazy configuration and configuration avoidance, which significantly improves build performance. " +
                    "See https://docs.gradle.org/current/userguide/lazy_configuration.html"
            );

    private static ArchCondition<JavaMethod> useProviderApiForInputOutputPropertiesMethods() {
        return new ArchCondition<JavaMethod>("use Provider API for input/output properties") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                checkMethodForProviderApiUsage(method.getOwner(), method, events);
            }
        };
    }

    private static void checkMethodForProviderApiUsage(JavaClass taskClass, JavaMethod method, ConditionEvents events) {
        JavaClass returnType = method.getRawReturnType();
        if (shouldUseProviderApi(returnType) && !isProviderApiType(returnType)) {
            String recommendation = getSpecificRecommendation(returnType, method);
            String message = String.format(
                    "Task %s has getter '%s()' returning type %s with input/output annotation. " +
                    "Use %s for lazy configuration.",
                    taskClass.getSimpleName(),
                    method.getName(),
                    returnType.getSimpleName(),
                    recommendation
            );
            events.add(SimpleConditionEvent.violated(method, message));
        }
    }

    private static boolean shouldUseProviderApi(JavaClass type) {
        String typeName = type.getName();
        return getMutableTypesThatShouldUseProvider().contains(typeName);
    }

    private static String getSpecificRecommendation(JavaClass type, JavaMethod method) {
        return getRecommendationForType(
                type,
                method.isAnnotatedWith(ANNOTATION_INPUT_FILE) || method.isAnnotatedWith(ANNOTATION_OUTPUT_FILE),
                method.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY) || method.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORY)
        );
    }

    private static String getRecommendationForType(JavaClass type, boolean isFileAnnotation, boolean isDirectoryAnnotation) {
        String typeName = type.getName();

        if (JAVA_IO_FILE.equals(typeName)) {
            if (isFileAnnotation) {
                return RECOMMENDATION_REGULAR_FILE_PROPERTY;
            }
            if (isDirectoryAnnotation) {
                return RECOMMENDATION_DIRECTORY_PROPERTY;
            }
            return RECOMMENDATION_REGULAR_FILE_PROPERTY + " or " + RECOMMENDATION_DIRECTORY_PROPERTY;
        }

        String mappedRecommendation = getTypeToProviderMap().get(typeName);
        if (mappedRecommendation != null) {
            return mappedRecommendation;
        }

        return "Property<" + type.getSimpleName() + ">";
    }

}
