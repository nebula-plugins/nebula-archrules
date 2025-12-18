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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.getters;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.hasRichPropertyReturnType;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.isProviderApiType;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.JavaModifier.PRIVATE;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

/**
 * Rules for Gradle task classes to ensure they use the Provider API for lazy configuration.
 * <p>
 * The Provider API ({@code Property<T>}, {@code Provider<T>}, {@code ConfigurableFileCollection})
 * enables lazy configuration and configuration avoidance, which are critical for build performance.
 */
@NullMarked
class GradleTaskProviderApiRule {

    private static final String JAVA_IO_FILE = "java.io.File";
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    private static final String JAVA_LANG_LONG = "java.lang.Long";
    private static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
    private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
    private static final String JAVA_LANG_FLOAT = "java.lang.Float";
    private static final String JAVA_UTIL_LIST = "java.util.List";
    private static final String JAVA_UTIL_SET = "java.util.Set";
    private static final String JAVA_UTIL_MAP = "java.util.Map";

    private static final String ANNOTATION_INPUT = "org.gradle.api.tasks.Input";
    private static final String ANNOTATION_INPUT_FILE = "org.gradle.api.tasks.InputFile";
    private static final String ANNOTATION_INPUT_FILES = "org.gradle.api.tasks.InputFiles";
    private static final String ANNOTATION_INPUT_DIRECTORY = "org.gradle.api.tasks.InputDirectory";
    private static final String ANNOTATION_OUTPUT_FILE = "org.gradle.api.tasks.OutputFile";
    private static final String ANNOTATION_OUTPUT_FILES = "org.gradle.api.tasks.OutputFiles";
    private static final String ANNOTATION_OUTPUT_DIRECTORY = "org.gradle.api.tasks.OutputDirectory";
    private static final String ANNOTATION_OUTPUT_DIRECTORIES = "org.gradle.api.tasks.OutputDirectories";

    private static final String RECOMMENDATION_REGULAR_FILE_PROPERTY = "RegularFileProperty";
    private static final String RECOMMENDATION_DIRECTORY_PROPERTY = "DirectoryProperty";
    private static final String RECOMMENDATION_LIST_PROPERTY = "ListProperty<T>";
    private static final String RECOMMENDATION_SET_PROPERTY = "SetProperty<T>";
    private static final String RECOMMENDATION_MAP_PROPERTY = "MapProperty<K, V>";

    private static class LazyHolder {
        private static final Set<String> MUTABLE_TYPES_THAT_SHOULD_USE_PROVIDER = new HashSet<>(Arrays.asList(
                JAVA_LANG_STRING,
                JAVA_LANG_INTEGER,
                JAVA_LANG_LONG,
                JAVA_LANG_BOOLEAN,
                JAVA_LANG_DOUBLE,
                JAVA_LANG_FLOAT,
                JAVA_IO_FILE,
                JAVA_UTIL_LIST,
                JAVA_UTIL_SET,
                JAVA_UTIL_MAP
        ));

        private static final Map<String, String> TYPE_TO_PROVIDER;

        static {
            Map<String, String> map = new HashMap<>();
            map.put(JAVA_UTIL_LIST, RECOMMENDATION_LIST_PROPERTY);
            map.put(JAVA_UTIL_SET, RECOMMENDATION_SET_PROPERTY);
            map.put(JAVA_UTIL_MAP, RECOMMENDATION_MAP_PROPERTY);
            TYPE_TO_PROVIDER = map;
        }
    }

    private static Set<String> getMutableTypesThatShouldUseProvider() {
        return LazyHolder.MUTABLE_TYPES_THAT_SHOULD_USE_PROVIDER;
    }

    private static Map<String, String> getTypeToProviderMap() {
        return LazyHolder.TYPE_TO_PROVIDER;
    }

    /**
     * Detects task input/output properties that should use Provider API types.
     * <p>
     * Task properties annotated with {@code @Input}, {@code @InputFile}, {@code @InputDirectory},
     * {@code @OutputFile}, {@code @OutputDirectory}, etc. should use Provider API types
     * ({@code Property<T>}, {@code RegularFileProperty}, {@code DirectoryProperty}, etc.)
     * instead of plain types for lazy configuration.
     */
    static final ArchRule PROVIDER_PROPERTIES = ArchRuleDefinition.priority(Priority.MEDIUM)
            .classes()
            .that().areAssignableTo("org.gradle.api.Task")
            .and().areNotInterfaces()
            .should(useProviderApiForInputOutputProperties())
            .allowEmptyShould(true)
            .because(
                    "Task input/output properties should use Provider API types (Property<T>, RegularFileProperty, " +
                    "DirectoryProperty, ConfigurableFileCollection) instead of plain types. " +
                    "This enables lazy configuration and configuration avoidance, which significantly improves build performance. " +
                    "See https://docs.gradle.org/current/userguide/lazy_configuration.html"
            );

    private static ArchCondition<JavaClass> useProviderApiForInputOutputProperties() {
        return new ArchCondition<JavaClass>("use Provider API for input/output properties") {
            @Override
            public void check(JavaClass taskClass, ConditionEvents events) {
                for (JavaField field : taskClass.getAllFields()) {
                    checkFieldForProviderApiUsage(taskClass, field, events);
                }

                for (JavaMethod method : taskClass.getAllMethods()) {
                    checkMethodForProviderApiUsage(taskClass, method, events);
                }
            }

            private void checkFieldForProviderApiUsage(JavaClass taskClass, JavaField field, ConditionEvents events) {
                if (!hasInputOutputAnnotation(field)) {
                    return;
                }

                if (shouldUseProviderApi(field.getRawType()) && !isProviderApiType(field.getRawType())) {
                    String recommendation = getSpecificRecommendation(field.getRawType(), field);
                    String message = String.format(
                            "Task %s has field '%s' of type %s with input/output annotation. " +
                            "Use %s for lazy configuration.",
                            taskClass.getSimpleName(),
                            field.getName(),
                            field.getRawType().getSimpleName(),
                            recommendation
                    );
                    events.add(SimpleConditionEvent.violated(field, message));
                }
            }

            private void checkMethodForProviderApiUsage(JavaClass taskClass, JavaMethod method, ConditionEvents events) {
                if (!hasInputOutputAnnotation(method)) {
                    return;
                }

                if (!method.getName().startsWith("get") || method.getRawParameterTypes().size() > 0) {
                    return;
                }

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

            private boolean hasInputOutputAnnotation(JavaField field) {
                return field.isAnnotatedWith(ANNOTATION_INPUT) ||
                       field.isAnnotatedWith(ANNOTATION_INPUT_FILE) ||
                       field.isAnnotatedWith(ANNOTATION_INPUT_FILES) ||
                       field.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY) ||
                       field.isAnnotatedWith(ANNOTATION_OUTPUT_FILE) ||
                       field.isAnnotatedWith(ANNOTATION_OUTPUT_FILES) ||
                       field.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORY) ||
                       field.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORIES);
            }

            private boolean hasInputOutputAnnotation(JavaMethod method) {
                return method.isAnnotatedWith(ANNOTATION_INPUT) ||
                       method.isAnnotatedWith(ANNOTATION_INPUT_FILE) ||
                       method.isAnnotatedWith(ANNOTATION_INPUT_FILES) ||
                       method.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY) ||
                       method.isAnnotatedWith(ANNOTATION_OUTPUT_FILE) ||
                       method.isAnnotatedWith(ANNOTATION_OUTPUT_FILES) ||
                       method.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORY) ||
                       method.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORIES);
            }

            private boolean shouldUseProviderApi(JavaClass type) {
                String typeName = type.getName();
                return getMutableTypesThatShouldUseProvider().contains(typeName);
            }

            private String getSpecificRecommendation(JavaClass type, JavaField field) {
                return getRecommendationForType(
                        type,
                        field.isAnnotatedWith(ANNOTATION_INPUT_FILE) || field.isAnnotatedWith(ANNOTATION_OUTPUT_FILE),
                        field.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY) || field.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORY)
                );
            }

            private String getSpecificRecommendation(JavaClass type, JavaMethod method) {
                return getRecommendationForType(
                        type,
                        method.isAnnotatedWith(ANNOTATION_INPUT_FILE) || method.isAnnotatedWith(ANNOTATION_OUTPUT_FILE),
                        method.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY) || method.isAnnotatedWith(ANNOTATION_OUTPUT_DIRECTORY)
                );
            }

            private String getRecommendationForType(JavaClass type, boolean isFileAnnotation, boolean isDirectoryAnnotation) {
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
        };
    }

    static final DescribedPredicate<JavaMethod> richTaskPropertyGetters = ArchPredicates.are(getters)
            .and(are(hasRichPropertyReturnType))
            .and(not(modifier(PRIVATE)))
            .and(not(annotatedWith("javax.inject.Inject")))
            .and(not(annotatedWith("org.gradle.api.tasks.options.OptionValues")))
            .and(not(declaredIn("org.gradle.api.Task")))
            .and(not(declaredIn("org.gradle.api.DefaultTask")))
            .and(not(declaredIn("org.gradle.api.internal.AbstractTask")))
            .as("task property getters");

    /**
     * Inspired by
     * {@link <a href="https://github.com/gradle/gradle/blob/master/testing/architecture-test/src/test/java/org/gradle/architecture/test/PropertyUsageArchitectureTest.java">Gradle</a>}
     */
    static final ArchRule ABSTRACT_GETTERS = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods().that(are(richTaskPropertyGetters))
            .should().haveModifier(JavaModifier.ABSTRACT)
            .allowEmptyShould(true)
            .because("task implementations should define properties as abstract getters");
}
