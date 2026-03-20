package com.netflix.nebula.archrules.spring;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.lang.conditions.ArchConditions.beAnnotatedWith;

@NullMarked
public class NoFieldInjectionRule {
    public static final ArchRule RULE = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noFields()
            .should(beAnnotatedWith("javax.inject.Inject"))
            .orShould(beAnnotatedWith("jakarta.inject.Inject"))
            .orShould(beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired"))
            .as("no fields should be annotated with @Inject or @Autowired")
            .allowEmptyShould(true)
            .because("constructor injection is preferred over field injection");
}
