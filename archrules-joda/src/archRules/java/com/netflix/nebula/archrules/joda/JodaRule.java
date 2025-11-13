package com.netflix.nebula.archrules.joda;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;

import java.util.Map;

public class JodaRule implements ArchRulesService {
    /**
     * This rule is a stop-gap to find all usages of Joda.
     */
    public static ArchRule jodaRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().dependOnClassesThat(resideInAPackage("org.joda.time.."))
            .allowEmptyShould(true)
            .as("No code should use Joda time library")
            .because("usage of Joda is deprecated. Please migrate to java.time.");

    @Override
    public Map<String, ArchRule> getRules() {
        return Map.of("jodaRule", jodaRule);
    }
}
