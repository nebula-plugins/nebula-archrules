package com.netflix.nebula.archrules.guava;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class GuavaRules implements ArchRulesService {
    static final ArchRule OPTIONAL = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should()
            .dependOnClassesThat().haveFullyQualifiedName("com.google.common.base.Optional")
            .because("Java Optional is preferred over Guava Optional");

    static final ArchRule COLLECTIONS = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.google.common.collect..")
            .because("Guava collections should not be used for compatibility reasons. " +
                     "Prefer Java or Kotlin standard library collections instead.");

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("guava optional", OPTIONAL);
        rules.put("guava collections", COLLECTIONS);
        return rules;
    }
}
