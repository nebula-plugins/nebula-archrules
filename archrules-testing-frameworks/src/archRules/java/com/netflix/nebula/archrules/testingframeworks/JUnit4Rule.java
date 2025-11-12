package com.netflix.nebula.archrules.testingframeworks;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;

import java.util.Map;

public class JUnit4Rule implements ArchRulesService {
    /**
     * This rule is a stop-gap to find all usages of JUnit4.
     */
    public static ArchRule junit4Rule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().dependOnClassesThat(resideInAPackage("org.junit..")
                    .and(resideOutsideOfPackage("org.junit.jupiter..")))
            .allowEmptyShould(true)
            .as("No code should use JUnit4 test packages")
            .because("usage of JUnit4 is deprecated. Please migrate to JUnit5 Jupiter.");

    @Override
    public Map<String, ArchRule> getRules() {
        return Map.of("junit4Rule", junit4Rule);
    }
}
