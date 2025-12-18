package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.areAnnotatedWithFileInputAnnotation;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.ANNOTATION_CACHEABLE_TASK;
import static com.netflix.nebula.archrules.gradleplugins.TypeConstants.ANNOTATION_PATH_SENSITIVE;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;

/**
 * Rules to ensure cacheable tasks properly declare path sensitivity.
 * <p>
 * Cacheable tasks must declare how file paths should be compared for cache key calculation.
 */
@NullMarked
public class GradleTaskCacheabilityRule {

    /**
     * Ensures that cacheable tasks declare path sensitivity on file inputs.
     * <p>
     * Cacheable tasks with file inputs must specify {@code @PathSensitive} to define
     * how file paths affect cache keys. Without this, tasks may not be relocatable
     * across different machines, breaking the build cache.
     */
    public static final ArchRule METHODS_PATH_SENSITIVITY = ArchRuleDefinition.priority(Priority.HIGH)
            .methods()
            .that(areAnnotatedWithFileInputAnnotation)
            .and().areDeclaredInClassesThat(are(annotatedWith(ANNOTATION_CACHEABLE_TASK)))
            .should().beAnnotatedWith(ANNOTATION_PATH_SENSITIVE)
            .allowEmptyShould(true)
            .because(
                    "Cacheable tasks with file inputs must declare @PathSensitive to specify how paths " +
                    "affect cache keys. This ensures build cache entries are relocatable across machines. " +
                    "See https://docs.gradle.org/current/userguide/build_cache.html#sec:task_output_caching_inputs"
            );

    /**
     * Ensures that cacheable tasks declare path sensitivity on file inputs.
     * <p>
     * Cacheable tasks with file inputs must specify {@code @PathSensitive} to define
     * how file paths affect cache keys. Without this, tasks may not be relocatable
     * across different machines, breaking the build cache.
     */
    public static final ArchRule FIELDS_PATH_SENSITIVITY = ArchRuleDefinition.priority(Priority.HIGH)
            .fields()
            .that(areAnnotatedWithFileInputAnnotation)
            .and().areDeclaredInClassesThat(are(annotatedWith(ANNOTATION_CACHEABLE_TASK)))
            .should().beAnnotatedWith(ANNOTATION_PATH_SENSITIVE)
            .allowEmptyShould(true)
            .because(
                    "Cacheable tasks with file inputs must declare @PathSensitive to specify how paths " +
                    "affect cache keys. This ensures build cache entries are relocatable across machines. " +
                    "See https://docs.gradle.org/current/userguide/build_cache.html#sec:task_output_caching_inputs"
            );
}
