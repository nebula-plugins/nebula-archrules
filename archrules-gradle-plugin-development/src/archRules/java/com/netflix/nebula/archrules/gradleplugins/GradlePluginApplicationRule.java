package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GradlePluginApplicationRule {
    public static final ArchRule APPLY_BY_ID = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should().callMethod("org.gradle.api.plugins.PluginContainer", "apply", "java.lang.Class")
            .orShould().callMethod("org.gradle.api.plugins.PluginManager", "apply", "java.lang.Class")
            .allowEmptyShould(true)
            .because("plugins should be applied by ID to ensure idempotency");
}
