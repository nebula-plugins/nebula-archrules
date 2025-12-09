package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.Map;

/**
 * Rules for cleaning up deprecated patterns after upgrading to Testcontainers 2.x.
 * <p>
 * These rules only apply when you're already on Testcontainers 2.x. They help you find
 * deprecated compatibility patterns that still work but should be migrated to the new canonical way.
 * <p>
 * <b>When to use:</b> After you've successfully upgraded to Testcontainers 2.x and want to
 * clean up deprecated imports and patterns.
 *
 * @see Testcontainers1xRule for rules to run BEFORE upgrading (finds breaking changes)
 * @see TestcontainersContainerModuleMapper for container-to-module package mappings
 */
@NullMarked
public class Testcontainers2xRule implements ArchRulesService {

    /**
     * Detects when you're using the old deprecated package locations for containers in Testcontainers 2.x.
     * <p>
     * In 2.x, Testcontainers moved container classes into their own module-specific packages.
     * For backward compatibility, they kept deprecated versions in the old {@code org.testcontainers.containers}
     * package. This rule helps you find those old imports so you can switch to the new canonical locations.
     * <p>
     * <b>What it checks:</b>
     * <ul>
     *   <li>Only flags container classes in {@code org.testcontainers.containers} package</li>
     *   <li>Ignores base classes like {@code GenericContainer} (they stay in the old package)</li>
     *   <li>Only reports violations when both old AND new packages exist (2.x only)</li>
     *   <li>On 1.x, this rule stays quiet since new packages don't exist yet</li>
     * </ul>
     * <p>
     * <b>Example - What to change:</b>
     * <pre>
     * // Before (deprecated in 2.x)
     * import org.testcontainers.containers.PostgreSQLContainer;
     *
     * // After (canonical in 2.x)
     * import org.testcontainers.postgresql.PostgreSQLContainer;
     * </pre>
     * <p>
     * <b>Why LOW priority?</b> The old packages still work in 2.x (just deprecated), so it won't break your build.
     * But you should migrate eventually since they might be removed in a future major version.
     *
     * @see TestcontainersContainerModuleMapper for the full container-to-module mapping
     */
    public static ArchRule legacyContainerPackageRule = ArchRuleDefinition.priority(Priority.LOW)
            .noClasses()
            .should().dependOnClassesThat(
                new DescribedPredicate<JavaClass>("deprecated container class in old package") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        if (!javaClass.getPackageName().equals("org.testcontainers.containers")) {
                            return false;
                        }
                        if (!javaClass.getSimpleName().endsWith("Container")) {
                            return false;
                        }

                        String simpleName = javaClass.getSimpleName();
                        if (simpleName.equals("GenericContainer") ||
                            simpleName.equals("ComposeContainer") ||
                            simpleName.equals("DockerComposeContainer")) {
                            return false;
                        }

                        if (javaClass.isInterface()) {
                            return false;
                        }

                        String newPackageName = TestcontainersContainerModuleMapper.getModulePackageName(simpleName);
                        if (newPackageName == null) {
                            return false;
                        }

                        try {
                            Class.forName(newPackageName + "." + simpleName);
                            return true;
                        } catch (ClassNotFoundException e) {
                            return false;
                        }
                    }
                }
            )
            .allowEmptyShould(true)
            .as("Code should use module-specific Testcontainers packages instead of org.testcontainers.containers")
            .because("In Testcontainers 2.x, container classes moved to module-specific packages " +
                     "(e.g., PostgreSQLContainer → org.testcontainers.postgresql.PostgreSQLContainer). " +
                     "The old locations are deprecated compatibility shims.");

    @Override
    public Map<String, ArchRule> getRules() {
        return Collections.singletonMap("testcontainers2x-legacyContainerPackage", legacyContainerPackageRule);
    }
}
