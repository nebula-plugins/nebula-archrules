package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("testcontainers1x-dockerComposeContainer", dockerComposeContainerRule);
        rules.put("testcontainers1x-containerIpAddressMethod", containerIpAddressMethodRule);
        return rules;
    }
}
