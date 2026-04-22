package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

public class DependencySubstitutionsAllMethodRule {

    public static final ArchRule DEPENDENCYSUBSTITUTIONS_ALL = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().callMethod(
                    "org.gradle.api.artifacts.DependencySubstitutions",
                    "all",
                    "org.gradle.api.Action")
            .allowEmptyShould(true)
            .because(
                    "Calling DependencySubstitutions.all causes the configuration to be resolved " +
                    "at task graph calculation time due to the possibility of project substitutions there. " +
                    "Instead, use DefaultDependencySubstitutions.addSubstitution or ResolutionStrategy.eachDependency."
            );
}
