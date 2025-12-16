package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.netflix.nebula.archrules.gradleplugins.Predicates.taskIsCreatedEagerly;

/**
 * Rules for Gradle plugin classes to ensure they use lazy task registration.
 * <p>
 * Lazy task registration (using {@code tasks.register()}) improves configuration time
 * by avoiding eager task creation. This is critical for build performance, especially
 * in large multi-module projects.
 */
@NullMarked
class GradlePluginLazyTaskRegistrationRule {


    /**
     * Prevents Plugin classes from using eager task creation methods.
     * <p>
     * Eager task creation (using {@code task()} or {@code tasks.create()}) creates
     * all tasks during configuration phase, even if they won't be executed.
     * Use {@code tasks.register()} instead for lazy task creation.
     */
    static final ArchRule LAZY_TASK_CREATION = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().implement("org.gradle.api.Plugin")
            .should().callMethodWhere(taskIsCreatedEagerly)
            .allowEmptyShould(true)
            .because(
                    "Plugins should use tasks.register() instead of task() or tasks.create() for lazy task registration. " +
                    "Eager task creation runs during configuration phase on EVERY build, significantly impacting performance. " +
                    "Lazy registration with tasks.register() only creates tasks when needed. " +
                    "See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html"
            );
}
