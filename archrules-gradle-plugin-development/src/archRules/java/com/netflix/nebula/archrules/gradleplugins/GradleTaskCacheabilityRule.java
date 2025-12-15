package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * Rules to ensure cacheable tasks properly declare path sensitivity.
 * <p>
 * Cacheable tasks must declare how file paths should be compared for cache key calculation.
 */
@NullMarked
public class GradleTaskCacheabilityRule implements ArchRulesService {

    private static final String ANNOTATION_CACHEABLE_TASK = "org.gradle.api.tasks.CacheableTask";
    private static final String ANNOTATION_INPUT_FILE = "org.gradle.api.tasks.InputFile";
    private static final String ANNOTATION_INPUT_FILES = "org.gradle.api.tasks.InputFiles";
    private static final String ANNOTATION_INPUT_DIRECTORY = "org.gradle.api.tasks.InputDirectory";
    private static final String ANNOTATION_PATH_SENSITIVE = "org.gradle.api.tasks.PathSensitive";

    /**
     * Ensures that cacheable tasks declare path sensitivity on file inputs.
     * <p>
     * Cacheable tasks with file inputs must specify {@code @PathSensitive} to define
     * how file paths affect cache keys. Without this, tasks may not be relocatable
     * across different machines, breaking the build cache.
     */
    public static final ArchRule cacheableTasksShouldDeclarePathSensitivity = ArchRuleDefinition.priority(Priority.HIGH)
            .classes()
            .that().areAnnotatedWith(ANNOTATION_CACHEABLE_TASK)
            .should(declarePathSensitivityOnFileInputs())
            .allowEmptyShould(true)
            .because(
                    "Cacheable tasks with file inputs must declare @PathSensitive to specify how paths " +
                    "affect cache keys. This ensures build cache entries are relocatable across machines. " +
                    "See https://docs.gradle.org/current/userguide/build_cache.html#sec:task_output_caching_inputs"
            );

    private static ArchCondition<JavaClass> declarePathSensitivityOnFileInputs() {
        return new ArchCondition<JavaClass>("declare @PathSensitive on file inputs") {
            @Override
            public void check(JavaClass taskClass, ConditionEvents events) {
                for (JavaField field : taskClass.getAllFields()) {
                    checkFieldPathSensitivity(taskClass, field, events);
                }

                for (JavaMethod method : taskClass.getAllMethods()) {
                    checkMethodPathSensitivity(taskClass, method, events);
                }
            }

            private void checkFieldPathSensitivity(JavaClass taskClass, JavaField field, ConditionEvents events) {
                if (!hasFileInputAnnotation(field)) {
                    return;
                }

                if (!field.isAnnotatedWith(ANNOTATION_PATH_SENSITIVE)) {
                    String message = String.format(
                            "Cacheable task %s has field '%s' with file input annotation but missing @PathSensitive. " +
                            "Add @PathSensitive to specify how file paths affect cache keys.",
                            taskClass.getSimpleName(),
                            field.getName()
                    );
                    events.add(SimpleConditionEvent.violated(field, message));
                }
            }

            private void checkMethodPathSensitivity(JavaClass taskClass, JavaMethod method, ConditionEvents events) {
                if (!hasFileInputAnnotation(method)) {
                    return;
                }

                if (!method.isAnnotatedWith(ANNOTATION_PATH_SENSITIVE)) {
                    String message = String.format(
                            "Cacheable task %s has method '%s()' with file input annotation but missing @PathSensitive. " +
                            "Add @PathSensitive to specify how file paths affect cache keys.",
                            taskClass.getSimpleName(),
                            method.getName()
                    );
                    events.add(SimpleConditionEvent.violated(method, message));
                }
            }

            private boolean hasFileInputAnnotation(JavaField field) {
                return field.isAnnotatedWith(ANNOTATION_INPUT_FILE) ||
                       field.isAnnotatedWith(ANNOTATION_INPUT_FILES) ||
                       field.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY);
            }

            private boolean hasFileInputAnnotation(JavaMethod method) {
                return method.isAnnotatedWith(ANNOTATION_INPUT_FILE) ||
                       method.isAnnotatedWith(ANNOTATION_INPUT_FILES) ||
                       method.isAnnotatedWith(ANNOTATION_INPUT_DIRECTORY);
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("gradle-task-cacheable-path-sensitivity", cacheableTasksShouldDeclarePathSensitivity);
        return rules;
    }
}
