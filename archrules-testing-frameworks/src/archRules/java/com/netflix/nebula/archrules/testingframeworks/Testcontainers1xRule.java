package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.AccessTarget;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;

/**
 * Architecture rules to detect Testcontainers 1.x patterns that need migration for 2.x compatibility.
 * <p>
 * Testcontainers 2.x introduced several breaking changes:
 * - Container classes moved to module-specific packages
 * - DockerComposeContainer renamed to ComposeContainer
 * - API method changes (getContainerIpAddress → getHost)
 * - Module artifacts require testcontainers- prefix
 * - JUnit 4 support removed (covered by JUnit4Rule)
 * <p>
 * These rules help teams prepare for and complete migration to Testcontainers 2.x.
 */
@NullMarked
public class Testcontainers1xRule implements ArchRulesService {

    /**
     * Rule to detect usage of DockerComposeContainer which was renamed to ComposeContainer in Testcontainers 2.x.
     * <b>Migration:</b> Replace {@code DockerComposeContainer} with {@code ComposeContainer}
     */
    public static ArchRule dockerComposeContainerRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().dependOnClassesThat().haveSimpleName("DockerComposeContainer")
            .allowEmptyShould(true)
            .as("No code should use DockerComposeContainer")
            .because("DockerComposeContainer was renamed to ComposeContainer in Testcontainers 2.x. " +
                     "Update imports to use org.testcontainers.containers.ComposeContainer");

    /**
     * Rule to detect usage of getContainerIpAddress() method which was replaced with getHost() in Testcontainers 2.x.
     * <b>Migration:</b> Replace {@code container.getContainerIpAddress()} with {@code container.getHost()}
     */
    public static ArchRule containerIpAddressMethodRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().callMethod("org.testcontainers.containers.ContainerState", "getContainerIpAddress")
            .allowEmptyShould(true)
            .as("No code should use getContainerIpAddress() method")
            .because("getContainerIpAddress() was replaced with getHost() in Testcontainers 2.x. " +
                     "Replace calls with getHost()");

    /**
     * Rule to detect usage of no-argument constructors on Testcontainers container classes which were removed in Testcontainers 2.x
     * <pre>
     * // Before - relies on default image
     * new PostgreSQLContainer()
     *
     * // After - explicit image with version using DockerImageName
     * new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
     * </pre>
     */
    public static ArchRule noArgConstructorRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().callConstructorWhere(target(new DescribedPredicate<AccessTarget>("no-arg Testcontainers container constructor") {
                @Override
                public boolean test(AccessTarget target) {
                    if (!(target instanceof AccessTarget.ConstructorCallTarget)) {
                        return false;
                    }
                    AccessTarget.ConstructorCallTarget constructorTarget = (AccessTarget.ConstructorCallTarget) target;
                    boolean isNoArg = constructorTarget.getRawParameterTypes().isEmpty();
                    boolean isTestcontainers = constructorTarget.getOwner().getPackageName().startsWith("org.testcontainers") &&
                                              constructorTarget.getOwner().getSimpleName().endsWith("Container");
                    return isNoArg && isTestcontainers;
                }
            }))
            .allowEmptyShould(true)
            .as("No code should use no-argument constructors on Testcontainers container classes")
            .because("Containers should specify explicit images with versions for reproducibility. " +
                     "Use explicit image specifications (e.g., new PostgreSQLContainer(DockerImageName.parse(\"postgres:16-alpine\")))");

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("testcontainers1x-dockerComposeContainer", dockerComposeContainerRule);
        rules.put("testcontainers1x-containerIpAddressMethod", containerIpAddressMethodRule);
        rules.put("testcontainers1x-noArgConstructor", noArgConstructorRule);
        return rules;
    }
}
