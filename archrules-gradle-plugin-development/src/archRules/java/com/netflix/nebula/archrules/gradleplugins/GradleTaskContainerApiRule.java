package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.callsMethodOn;
import static com.netflix.nebula.archrules.gradleplugins.Predicates.callsMethodOnAny;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;

/**
 * Rules for Gradle plugin classes to ensure they use lazy task access APIs.
 * <p>
 * Using lazy APIs like {@code tasks.named()} instead of eager APIs like {@code tasks.getByName()}
 * improves configuration time by avoiding unnecessary task realization.
 */
@NullMarked
class GradleTaskContainerApiRule {

    private static final DescribedPredicate<JavaAccess<?>> callsGetByName =
            callsMethodOnAny("getByName", "org.gradle.api.tasks.TaskContainer", "org.gradle.api.tasks.TaskCollection");

    private static final DescribedPredicate<JavaAccess<?>> callsWithType =
            callsMethodOnAny("withType", "org.gradle.api.tasks.TaskContainer", "org.gradle.api.tasks.TaskCollection");

    /**
     * Prevents Plugin classes from using eager task lookup methods.
     * <p>
     * Eager task lookup using {@code tasks.getByName()} forces task realization
     * during configuration phase, even if the task won't be executed.
     * Use {@code tasks.named()} instead for lazy task lookup.
     */
    public static final ArchRule USE_NAMED_INSTEAD_OF_GET_BY_NAME = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().callMethodWhere(callsGetByName)
            .allowEmptyShould(true)
            .because(
                    "Plugins should use tasks.named() instead of tasks.getByName() for lazy task lookup. " +
                    "getByName() forces immediate task realization during configuration phase, impacting performance. " +
                    "named() returns a TaskProvider that delays task creation until needed. " +
                    "See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html"
            );

    private static final DescribedPredicate<JavaAccess<?>> callsAll =
            callsMethodOn("all", "org.gradle.api.DomainObjectCollection")
            .and(not(targetOwner(assignableTo("org.gradle.api.artifacts.ConfigurationContainer"))));

    /**
     * Prevents Plugin classes from using eager task configuration methods.
     * <p>
     * Using {@code tasks.all()} or {@code tasks.withType().all()} configures all matching tasks
     * immediately, even if they won't be executed. Use {@code configureEach()} instead for
     * lazy task configuration.
     */
    public static final ArchRule USE_CONFIGURE_EACH_INSTEAD_OF_ALL = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().callMethodWhere(callsAll)
            .allowEmptyShould(true)
            .because(
                    "Plugins should use configureEach() instead of all() for lazy task configuration. " +
                    "all() realizes and configures all matching tasks immediately during configuration phase. " +
                    "configureEach() only configures tasks when they are realized. " +
                    "See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html"
            );
}
