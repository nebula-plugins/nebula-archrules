package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.AccessTarget;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;

/**
 * Rules to help you migrate from Testcontainers 1.x to 2.x without breaking everything.
 * <p>
 * Testcontainers 2.x changed a bunch of stuff. Run these rules BEFORE upgrading to find what
 * will break, so you can fix issues while still on 1.x.
 *
 * <h2>What breaking changes are covered:</h2>
 * <ul>
 *   <li>DockerComposeContainer → ComposeContainer (class renamed)</li>
 *   <li>getContainerIpAddress() → getHost() (method renamed)</li>
 *   <li>No-arg constructors removed (must specify explicit image versions)</li>
 * </ul>
 * <p>
 * <b>After upgrading to 2.x:</b> Use {@link Testcontainers2xRule} to find deprecated patterns
 * that still work but should be cleaned up (like old package imports).
 *
 * @see Testcontainers2xRule for cleanup rules to run AFTER upgrading to 2.x
 * @see <a href="https://java.testcontainers.org/changelog/#-testcontainers-for-java-2-0-0">Official 2.0.0 Release Notes</a>
 */
@NullMarked
public class Testcontainers1xRule implements ArchRulesService {

    /**
     * Finds usage of {@code DockerComposeContainer} which got renamed in 2.x.
     * <p>
     * In Testcontainers 2.x, they simplified the name from {@code DockerComposeContainer} to just
     * {@code ComposeContainer}. This rule catches any code still using the old name.
     * <p>
     * <b>The fix:</b> Replace {@code DockerComposeContainer} with {@code ComposeContainer} everywhere.
     */
    public static final ArchRule dockerComposeContainerRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().dependOnClassesThat().haveSimpleName("DockerComposeContainer")
            .allowEmptyShould(true)
            .as("No code should use DockerComposeContainer")
            .because("DockerComposeContainer was renamed to ComposeContainer in Testcontainers 2.x. " +
                     "Update imports to use org.testcontainers.containers.ComposeContainer");

    /**
     * Finds calls to {@code getContainerIpAddress()} which got replaced in 2.x.
     * <p>
     * Testcontainers 2.x renamed this method to {@code getHost()} for clarity (since it doesn't always
     * return an IP address - sometimes it's a hostname or localhost).
     * <p>
     * <b>The fix:</b> Change {@code container.getContainerIpAddress()} to {@code container.getHost()}.
     */
    public static final ArchRule containerIpAddressMethodRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().callMethod("org.testcontainers.containers.ContainerState", "getContainerIpAddress")
            .allowEmptyShould(true)
            .as("No code should use getContainerIpAddress() method")
            .because("getContainerIpAddress() was replaced with getHost() in Testcontainers 2.x. " +
                     "Replace calls with getHost()");

    /**
     * Finds no-argument constructors on container classes - these don't exist in 2.x.
     * <p>
     * Testcontainers 2.x removed no-arg constructors to force you to specify explicit image versions.
     * This prevents surprises when "latest" changes or when you're running tests months later and
     * the default image has breaking changes.
     * <p>
     * <b>What to change:</b>
     * <pre>
     * // Before (worked in 1.x, breaks in 2.x)
     * new PostgreSQLContainer()
     *
     * // After (works in both, more reproducible)
     * new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
     * </pre>
     * <p>
     */
    public static final ArchRule noArgConstructorRule = ArchRuleDefinition.priority(Priority.MEDIUM)
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
