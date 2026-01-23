package com.netflix.nebula.archrules.nullability;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.properties.HasModifiers;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import java.util.HashMap;
import java.util.Map;

import static com.netflix.nebula.archrules.nullability.HaveNoTests.haveNoTests;
import static com.tngtech.archunit.lang.conditions.ArchConditions.fullyQualifiedName;

public class NebulaNullabilityArchRules implements ArchRulesService {
    static final ArchRule PUBLIC_CLASSES_SHOULD_BE_NULL_MARKED = ArchRuleDefinition.priority(Priority.MEDIUM)
            .classes().that()
            .areTopLevelClasses()
            .and().arePublic()
            .and().containAnyMembersThat(HasModifiers.Predicates.modifier(JavaModifier.PUBLIC))
            .and(haveNoTests())
            .and().areNotAnnotatedWith("kotlin.Metadata")
            .should().beAnnotatedWith("org.jspecify.annotations.NullMarked")
            .allowEmptyShould(true)
            .because("public classes should be null marked");
    static final ArchRule UPGRADE_LEGACY_JETBRAINS = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().areAnnotatedWith("org.jspecify.annotations.NullMarked")
            .should()
            .dependOnClassesThat(fullyQualifiedName("org.jetbrains.annotations.Nullable"))
            .orShould().dependOnClassesThat(fullyQualifiedName("org.jetbrains.annotations.NotNull"))
            .allowEmptyShould(true)
            .because("Only JSpecify annotations should be used on @NullMarked classes");
    static final ArchRule UPGRADE_LEGACY_SPRING_FRAMEWORK = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().areAnnotatedWith("org.jspecify.annotations.NullMarked")
            .should()
            .dependOnClassesThat(fullyQualifiedName("org.springframework.lang.Nullable"))
            .orShould().dependOnClassesThat(fullyQualifiedName("org.springframework.lang.NonNull"))
            .allowEmptyShould(true)
            .because("Only JSpecify annotations should be used on @NullMarked classes");
    static final ArchRule UPGRADE_LEGACY_JAVAX = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().areAnnotatedWith("org.jspecify.annotations.NullMarked")
            .should()
            .dependOnClassesThat(fullyQualifiedName("javax.annotation.Nullable"))
            .orShould().dependOnClassesThat(fullyQualifiedName("javax.annotation.Nonnull"))
            .allowEmptyShould(true)
            .because("Only JSpecify annotations should be used on @NullMarked classes");
    static final ArchRule UPGRADE_LEGACY_JAKARTA = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .that().areAnnotatedWith("org.jspecify.annotations.NullMarked")
            .should()
            .dependOnClassesThat(fullyQualifiedName("jakarta.annotation.Nullable"))
            .orShould().dependOnClassesThat(fullyQualifiedName("jakarta.annotation.Nonnull"))
            .allowEmptyShould(true)
            .because("Only JSpecify annotations should be used on @NullMarked classes");
    static final ArchRule NO_OPTIONAL_CLASS_FIELDS = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noFields()
            .should().haveRawType("java.util.Optional")
            .allowEmptyShould(true)
            .because("Class fields should not be Optional, use Nullable instead");
    static final ArchRule NO_OPTIONAL_METHOD_PARAMETERS = ArchRuleDefinition.priority(Priority.LOW)
            .noMethods()
            .should().haveRawParameterTypes("java.util.Optional")
            .allowEmptyShould(true)
            .because("Method parameters should not be Optional");

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("public classes should be @NullMarked", PUBLIC_CLASSES_SHOULD_BE_NULL_MARKED);
        rules.put("upgrade legacy jetbrains annotations", UPGRADE_LEGACY_JETBRAINS);
        rules.put("upgrade legacy spring annotations", UPGRADE_LEGACY_SPRING_FRAMEWORK);
        rules.put("upgrade legacy javax annotations", UPGRADE_LEGACY_JAVAX);
        rules.put("upgrade legacy jakarta annotations", UPGRADE_LEGACY_JAKARTA);
        rules.put("no Optional class fields", NO_OPTIONAL_CLASS_FIELDS);
        rules.put("no Optional method parameters", NO_OPTIONAL_METHOD_PARAMETERS);
        return rules;
    }
}
