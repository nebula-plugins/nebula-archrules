package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import java.util.Collections;
import java.util.Map;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

public class DeprecationRule implements ArchRulesService {

    /**
     * This rule is a stop-gap to find all deprecations. This is likely very noisy, so is a Low priority.
     * It is recommended to craft more targeted deprecation rules when targeting actual removal of deprecated code.
     */
    public static ArchRule deprecationRule = ArchRuleDefinition.priority(Priority.LOW)
            .noClasses()
            .should().dependOnClassesThat().areAnnotatedWith(Deprecated.class)
            .orShould().accessTargetWhere(targetOwner(annotatedWith(Deprecated.class)))
            .orShould().accessTargetWhere(target(annotatedWith(Deprecated.class)))
            .allowEmptyShould(true)
            .as("No code should reference deprecated APIs")
            .because("usage of deprecated APIs introduces risk that future upgrades and migrations will be blocked");

    @Override
    public Map<String, ArchRule> getRules() {
        return Collections.singletonMap("deprecated", deprecationRule);
    }
}
