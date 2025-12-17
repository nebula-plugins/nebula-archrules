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
 * Rules to prevent usage of internal Gradle APIs.
 * <p>
 * Internal Gradle APIs are not part of the public API contract and may change
 * or be removed without notice between Gradle versions.
 */
@NullMarked
public class GradleInternalApiRule implements ArchRulesService {

    private static final String GRADLE_PACKAGE = "org.gradle";
    private static final String INTERNAL_PACKAGE_MARKER = ".internal.";

    /**
     * Prevents plugins from using internal Gradle APIs.
     * <p>
     * Internal Gradle APIs (packages containing {@code .internal.}) are not stable
     * and may change or be removed between versions without notice. Use only public
     * Gradle APIs to ensure compatibility across Gradle versions.
     */
    public static final ArchRule pluginsShouldNotUseInternalGradleApis = ArchRuleDefinition.priority(Priority.HIGH)
            .classes()
            .that().implement("org.gradle.api.Plugin")
            .should(notUseInternalGradleApis())
            .allowEmptyShould(true)
            .because(
                    "Plugins should not use internal Gradle APIs (packages containing '.internal.'). " +
                    "Internal APIs are not stable and may change or be removed without notice. " +
                    "Use only public Gradle APIs documented at https://docs.gradle.org/current/javadoc/"
            );

    /**
     * Prevents tasks from using internal Gradle APIs.
     * <p>
     * Internal Gradle APIs (packages containing {@code .internal.}) are not stable
     * and may change or be removed between versions without notice. Use only public
     * Gradle APIs to ensure compatibility across Gradle versions.
     */
    public static final ArchRule tasksShouldNotUseInternalGradleApis = ArchRuleDefinition.priority(Priority.HIGH)
            .classes()
            .that().areAssignableTo("org.gradle.api.Task")
            .and().areNotInterfaces()
            .should(notUseInternalGradleApis())
            .allowEmptyShould(true)
            .because(
                    "Tasks should not use internal Gradle APIs (packages containing '.internal.'). " +
                    "Internal APIs are not stable and may change or be removed without notice. " +
                    "Use only public Gradle APIs documented at https://docs.gradle.org/current/javadoc/"
            );

    private static ArchCondition<JavaClass> notUseInternalGradleApis() {
        return new ArchCondition<JavaClass>("not use internal Gradle APIs") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaAccess<?> access : javaClass.getAccessesFromSelf()) {
                    if (isInternalGradleApi(access)) {
                        String message = String.format(
                                "Class %s uses internal Gradle API: %s. " +
                                "Internal APIs (packages containing '.internal.') are not stable and may change without notice. " +
                                "Use public Gradle APIs instead.",
                                javaClass.getSimpleName(),
                                access.getDescription()
                        );
                        events.add(SimpleConditionEvent.violated(access, message));
                    }
                }
            }

            private boolean isInternalGradleApi(JavaAccess<?> access) {
                String targetPackage = access.getTargetOwner().getPackageName();
                return targetPackage.startsWith(GRADLE_PACKAGE) &&
                       targetPackage.contains(INTERNAL_PACKAGE_MARKER);
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("gradle-plugin-no-internal-apis", pluginsShouldNotUseInternalGradleApis);
        rules.put("gradle-task-no-internal-apis", tasksShouldNotUseInternalGradleApis);
        return rules;
    }
}
