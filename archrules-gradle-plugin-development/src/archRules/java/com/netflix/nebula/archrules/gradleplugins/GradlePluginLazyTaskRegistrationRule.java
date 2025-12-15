package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Rules for Gradle plugin classes to ensure they use lazy task registration.
 * <p>
 * Lazy task registration (using {@code tasks.register()}) improves configuration time
 * by avoiding eager task creation. This is critical for build performance, especially
 * in large multi-module projects.
 */
@NullMarked
public class GradlePluginLazyTaskRegistrationRule implements ArchRulesService {

    private static final Set<String> EAGER_TASK_CREATION_METHODS = new HashSet<>(Arrays.asList(
            "task",
            "create"
    ));

    /**
     * Prevents Plugin classes from using eager task creation methods.
     * <p>
     * Eager task creation (using {@code task()} or {@code tasks.create()}) creates
     * all tasks during configuration phase, even if they won't be executed.
     * Use {@code tasks.register()} instead for lazy task creation.
     */
    public static final ArchRule pluginsShouldUseLazyTaskRegistration = ArchRuleDefinition.priority(Priority.MEDIUM)
            .classes()
            .that().implement("org.gradle.api.Plugin")
            .should(useLazyTaskRegistration())
            .allowEmptyShould(true)
            .because(
                    "Plugins should use tasks.register() instead of task() or tasks.create() for lazy task registration. " +
                    "Eager task creation runs during configuration phase on EVERY build, significantly impacting performance. " +
                    "Lazy registration with tasks.register() only creates tasks when needed. " +
                    "See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html"
            );

    private static ArchCondition<JavaClass> useLazyTaskRegistration() {
        return new ArchCondition<JavaClass>("use lazy task registration (tasks.register())") {
            @Override
            public void check(JavaClass pluginClass, ConditionEvents events) {
                pluginClass.getMethodCallsFromSelf().forEach(call -> {
                    if (isEagerTaskCreation(call)) {
                        String message = String.format(
                                "Plugin %s uses eager task creation with %s.%s() at %s. " +
                                "Use tasks.register() instead for lazy task registration.",
                                pluginClass.getSimpleName(),
                                call.getTargetOwner().getSimpleName(),
                                call.getName(),
                                call.getDescription()
                        );
                        events.add(SimpleConditionEvent.violated(call, message));
                    }
                });
            }

            private boolean isEagerTaskCreation(JavaMethodCall call) {
                String methodName = call.getName();

                if (!EAGER_TASK_CREATION_METHODS.contains(methodName)) {
                    return false;
                }

                JavaClass targetOwner = call.getTargetOwner();
                return targetOwner.isAssignableTo("org.gradle.api.Project") ||
                       targetOwner.isAssignableTo("org.gradle.api.tasks.TaskContainer");
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("gradle-plugin-lazy-task-registration", pluginsShouldUseLazyTaskRegistration);
        return rules;
    }
}
