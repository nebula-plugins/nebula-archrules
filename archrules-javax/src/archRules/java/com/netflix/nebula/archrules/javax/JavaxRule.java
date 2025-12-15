package com.netflix.nebula.archrules.javax;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.Map;

@NullMarked
public class JavaxRule implements ArchRulesService {
    /**
     * This rule is a stop-gap to find all usages of Javax.
     */
    public static final ArchRule javaxRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().dependOnClassesThat().resideInAPackage("javax..")
            .allowEmptyShould(true)
            .as("No code should use Javax library")
            .because("usage of Javax is deprecated. Please migrate to Jakarta.");

    @Override
    public Map<String, ArchRule> getRules() {
        return Collections.singletonMap("javaxRule", javaxRule);
    }
}
