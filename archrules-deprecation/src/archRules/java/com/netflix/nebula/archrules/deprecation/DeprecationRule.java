package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

import static com.netflix.nebula.archrules.common.CanBeAnnotated.Predicates.deprecated;
import static com.netflix.nebula.archrules.common.CanBeAnnotated.Predicates.deprecatedForRemoval;
import static com.netflix.nebula.archrules.common.Dependency.Predicates.resideInSamePackage;
import static com.netflix.nebula.archrules.common.JavaAccess.Predicates.targetHasOwnerInSamePackage;
import static com.netflix.nebula.archrules.common.JavaClass.Conditions.haveAnyDependenciesThat;
import static com.tngtech.archunit.base.DescribedPredicate.doNot;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.Dependency.Predicates.dependencyTarget;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.is;

@NullMarked
public class DeprecationRule implements ArchRulesService {

    /**
     * This rule is a stop-gap to find all deprecations. This is likely very noisy, so is a Low priority.
     * It is recommended to craft more targeted deprecation rules when targeting actual removal of deprecated code.
     * <p>
     * This rule catches:
     * - Java @Deprecated annotations
     * - Kotlin @Deprecated annotations
     * - Kotlin @DeprecatedSinceKotlin annotations
     */
    public static final ArchRule deprecationRule = ArchRuleDefinition.priority(Priority.LOW)
            .noClasses()
            .should(haveAnyDependenciesThat(doNot(resideInSamePackage())
                    .and(dependencyTarget(is(deprecated())))))
            .orShould().accessTargetWhere(not(targetHasOwnerInSamePackage())
                    .and(target(is(deprecated())).or(targetOwner(is(deprecated())))))
            .allowEmptyShould(true)
            .because("usage of deprecated APIs introduces risk that future upgrades and migrations will be blocked");

    public static final ArchRule deprecationForRemovalRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should(haveAnyDependenciesThat(doNot(resideInSamePackage())
                    .and(dependencyTarget(is(deprecatedForRemoval())))))
            .orShould().accessTargetWhere(not(targetHasOwnerInSamePackage())
                    .and(target(is(deprecatedForRemoval())).or(targetOwner(is(deprecatedForRemoval())))))
            .allowEmptyShould(true)
            .because("these APIs are scheduled for removal and usage will block future upgrades");

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new java.util.HashMap<>();
        rules.put("deprecated", deprecationRule);
        rules.put("deprecatedForRemoval", deprecationForRemovalRule);
        return rules;
    }
}
