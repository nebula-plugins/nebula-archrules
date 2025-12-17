package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
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
 * Rules to prevent usage of deprecated Gradle APIs.
 * <p>
 * Using deprecated Gradle APIs will cause build failures in future Gradle versions.
 */
@NullMarked
public class GradleDeprecatedApiRule implements ArchRulesService {

    private static final String GRADLE_API_PACKAGE = "org.gradle";

    /**
     * Prevents plugins from using deprecated Gradle APIs.
     * <p>
     * Deprecated Gradle APIs will be removed in future versions, causing build failures.
     * Replace deprecated APIs with their modern equivalents as documented in Gradle's
     * upgrade guides.
     */
    public static final ArchRule pluginsShouldNotUseDeprecatedGradleApis = ArchRuleDefinition.priority(Priority.MEDIUM)
            .classes()
            .that().implement("org.gradle.api.Plugin")
            .should(notUseDeprecatedGradleApis())
            .allowEmptyShould(true)
            .because(
                    "Plugins should not use deprecated Gradle APIs as they will be removed in future versions. " +
                    "Consult Gradle upgrade guides for modern alternatives. " +
                    "See https://docs.gradle.org/current/userguide/upgrading_version_8.html"
            );

    /**
     * Prevents tasks from using deprecated Gradle APIs.
     * <p>
     * Deprecated Gradle APIs will be removed in future versions, causing build failures.
     * Replace deprecated APIs with their modern equivalents as documented in Gradle's
     * upgrade guides.
     */
    public static final ArchRule tasksShouldNotUseDeprecatedGradleApis = ArchRuleDefinition.priority(Priority.MEDIUM)
            .classes()
            .that().areAssignableTo("org.gradle.api.Task")
            .and().areNotInterfaces()
            .should(notUseDeprecatedGradleApis())
            .allowEmptyShould(true)
            .because(
                    "Tasks should not use deprecated Gradle APIs as they will be removed in future versions. " +
                    "Consult Gradle upgrade guides for modern alternatives. " +
                    "See https://docs.gradle.org/current/userguide/upgrading_version_8.html"
            );

    private static ArchCondition<JavaClass> notUseDeprecatedGradleApis() {
        return new ArchCondition<JavaClass>("not use deprecated Gradle APIs") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaAccess<?> access : javaClass.getAccessesFromSelf()) {
                    if (isDeprecatedGradleApi(access)) {
                        String message = String.format(
                                "Class %s uses deprecated Gradle API: %s. " +
                                "This API will be removed in a future Gradle version. " +
                                "Consult Gradle upgrade guides for alternatives.",
                                javaClass.getSimpleName(),
                                access.getDescription()
                        );
                        events.add(SimpleConditionEvent.violated(access, message));
                    }
                }
            }

            private boolean isDeprecatedGradleApi(JavaAccess<?> access) {
                String targetOwnerName = access.getTargetOwner().getName();

                if (!targetOwnerName.startsWith(GRADLE_API_PACKAGE)) {
                    return false;
                }

                return access.getTarget().isAnnotatedWith(Deprecated.class);
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("gradle-plugin-no-deprecated-apis", pluginsShouldNotUseDeprecatedGradleApis);
        rules.put("gradle-task-no-deprecated-apis", tasksShouldNotUseDeprecatedGradleApis);
        return rules;
    }
}
