package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaFieldAccess;
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
 * Rules for Gradle task action methods to ensure Gradle 10 compatibility.
 */
@NullMarked
public class GradleTaskActionRule implements ArchRulesService {

    /**
     * Prevents {@code @TaskAction} methods from accessing {@code Project}.
     * <p>
     * Accessing {@code Project} in task actions breaks configuration cache and will
     * cause runtime errors in Gradle 10+. Move Project access to configuration time
     * (constructor/initializer) and use task properties instead.
     */
    public static final ArchRule taskActionShouldNotAccessProject = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods()
            .that(areAnnotatedWithTaskAction())
            .should(notAccessProject())
            .allowEmptyShould(true)
            .because(
                    "Accessing Project in @TaskAction methods breaks configuration cache and will be removed in Gradle 10. " +
                    "Move Project access to task configuration time and use task inputs/properties instead. " +
                    "See https://docs.gradle.org/9.2.0/userguide/upgrading_version_7.html#task_project"
            );

    /**
     * Prevents {@code @TaskAction} methods from calling {@code getTaskDependencies()}.
     * <p>
     * Calling {@code getTaskDependencies()} in task actions breaks configuration cache and will
     * cause runtime errors in Gradle 10+. Task dependencies should be declared at configuration time.
     */
    public static final ArchRule taskActionShouldNotCallGetTaskDependencies = ArchRuleDefinition.priority(Priority.MEDIUM)
            .methods()
            .that(areAnnotatedWithTaskAction())
            .should(notCallGetTaskDependencies())
            .allowEmptyShould(true)
            .because(
                    "Calling getTaskDependencies() in @TaskAction methods breaks configuration cache and will be removed in Gradle 10. " +
                    "Declare task dependencies at configuration time instead. " +
                    "See https://docs.gradle.org/9.2.0/userguide/upgrading_version_7.html#task_dependencies"
            );

    private static DescribedPredicate<JavaMethod> areAnnotatedWithTaskAction() {
        return new DescribedPredicate<JavaMethod>("are annotated with @TaskAction") {
            @Override
            public boolean test(JavaMethod method) {
                return method.isAnnotatedWith("org.gradle.api.tasks.TaskAction");
            }
        };
    }

    private static ArchCondition<JavaMethod> notAccessProject() {
        return notAccessType("Project", "org.gradle.api.Project", "getProject");
    }

    private static ArchCondition<JavaMethod> notCallGetTaskDependencies() {
        return notAccessType("TaskDependency", "org.gradle.api.tasks.TaskDependency", "getTaskDependencies");
    }

    private static ArchCondition<JavaMethod> notAccessType(String displayName, String fullyQualifiedClassName, String getterMethodName) {
        return new ArchCondition<JavaMethod>("not access " + displayName) {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (JavaAccess<?> access : method.getAccessesFromSelf()) {
                    if (isTargetTypeAccess(access) || isGetterCall(access) || isTargetTypeFieldAccess(access)) {
                        String message = String.format(
                                "Method %s.%s() accesses %s at %s",
                                method.getOwner().getSimpleName(),
                                method.getName(),
                                displayName,
                                access.getDescription()
                        );
                        events.add(SimpleConditionEvent.violated(access, message));
                    }
                }
            }

            private boolean isTargetTypeAccess(JavaAccess<?> access) {
                return fullyQualifiedClassName.equals(access.getTargetOwner().getFullName());
            }

            private boolean isGetterCall(JavaAccess<?> access) {
                if (!getterMethodName.equals(access.getName())) {
                    return false;
                }
                return access.getTargetOwner().isAssignableTo("org.gradle.api.Task");
            }

            private boolean isTargetTypeFieldAccess(JavaAccess<?> access) {
                if (access instanceof JavaFieldAccess) {
                    JavaFieldAccess fieldAccess = (JavaFieldAccess) access;
                    return fieldAccess.getTarget().getRawType().isAssignableTo(fullyQualifiedClassName);
                }
                return false;
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("gradle-task-action-project-access", taskActionShouldNotAccessProject);
        rules.put("gradle-task-action-task-dependencies", taskActionShouldNotCallGetTaskDependencies);
        return rules;
    }
}
