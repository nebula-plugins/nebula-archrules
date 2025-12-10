package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.Map;

/**
 * Rules for cleaning up deprecated patterns after upgrading to Testcontainers 2.x.
 * <p>
 * Run these rules after upgrading to find deprecated compatibility patterns that should
 * be migrated to the new canonical locations.
 *
 * @see Testcontainers1xRule for rules to run BEFORE upgrading (finds breaking changes)
 * @see TestcontainersContainerModuleMapper for container-to-module package mappings
 */
@NullMarked
public class Testcontainers2xRule implements ArchRulesService {

    /**
     * Detects usage of deprecated container package locations in Testcontainers 2.x.
     * <p>
     * In 2.x, container classes moved to module-specific packages. The old {@code org.testcontainers.containers}
     * package still works but is deprecated. This rule only triggers on 2.x (detects via {@code @Deprecated} annotation).
     * <p>
     * <b>Example:</b>
     * <pre>
     * // Before (deprecated in 2.x)
     * import org.testcontainers.containers.PostgreSQLContainer;
     *
     * // After (canonical in 2.x)
     * import org.testcontainers.postgresql.PostgreSQLContainer;
     * </pre>
     * <p>
     * <b>Priority LOW:</b> Old packages still work, but migrate before a future major version removes them.
     *
     * @see TestcontainersContainerModuleMapper for the full container-to-module mapping
     */
    public static ArchRule legacyContainerPackageRule = ArchRuleDefinition.priority(Priority.LOW)
            .classes()
            .should(notDependOnDeprecatedContainerPackages())
            .allowEmptyShould(true)
            .as("Code should use module-specific Testcontainers packages instead of org.testcontainers.containers")
            .because("In Testcontainers 2.x, container classes moved to module-specific packages " +
                     "(e.g., PostgreSQLContainer → org.testcontainers.postgresql.PostgreSQLContainer). " +
                     "The old locations are deprecated compatibility shims.");

    private static ArchCondition<JavaClass> notDependOnDeprecatedContainerPackages() {
        return new ArchCondition<JavaClass>("not depend on deprecated Testcontainers container packages") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Check dependencies of this class for deprecated containers
                javaClass.getDirectDependenciesFromSelf().forEach(dependency -> {
                    JavaClass targetClass = dependency.getTargetClass();
                    if (isDeprecatedContainerClass(targetClass)) {
                        String message = String.format(
                                "%s depends on deprecated package %s. " +
                                        "Use %s instead.",
                                javaClass.getName(),
                                targetClass.getName(),
                                getNewPackageName(targetClass)
                        );
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                });
            }

            private boolean isDeprecatedContainerClass(JavaClass javaClass) {
                // In Testcontainers 2.x, old package classes are marked with @Deprecated
                // In 1.x, they're not deprecated yet
                // So we only flag violations if the class is actually deprecated
                if (!javaClass.isAnnotatedWith(Deprecated.class)) {
                    return false;
                }

                if (!javaClass.getPackageName().equals("org.testcontainers.containers")) {
                    return false;
                }
                if (!javaClass.getSimpleName().endsWith("Container")) {
                    return false;
                }

                String simpleName = javaClass.getSimpleName();
                // Exclude base classes that legitimately stay in the old package
                if (simpleName.equals("GenericContainer") ||
                        simpleName.equals("ComposeContainer") ||
                        simpleName.equals("DockerComposeContainer")) {
                    return false;
                }

                if (javaClass.isInterface()) {
                    return false;
                }

                // Check if a module-specific package mapping exists for this container
                String newPackageName = TestcontainersContainerModuleMapper.getModulePackageName(simpleName);
                return newPackageName != null;
            }

            private String getNewPackageName(JavaClass javaClass) {
                String newPackage = TestcontainersContainerModuleMapper.getModulePackageName(javaClass.getSimpleName());
                return newPackage != null ? newPackage + "." + javaClass.getSimpleName() : javaClass.getName();
            }
        };
    }

    @Override
    public Map<String, ArchRule> getRules() {
        return Collections.singletonMap("testcontainers2x-legacyContainerPackage", legacyContainerPackageRule);
    }
}
