package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.deprecated.ClassInDeprecatedPackage;
import com.netflix.nebula.archrules.common.other.ClassInOtherPackage;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.common.Dependency.Predicates.resideInSamePackage;
import static com.netflix.nebula.archrules.common.JavaClass.Conditions.haveAnyDependenciesThat;
import static com.netflix.nebula.archrules.common.JavaClass.Predicates.resideInAPackageThat;
import static com.netflix.nebula.archrules.common.Util.scanClass;
import static com.netflix.nebula.archrules.common.Util.scanClasses;
import static com.netflix.nebula.archrules.common.Util.scanClassesWithPackage;
import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaClassTest {

    @Test
    public void test_same() {
        final var rule = ArchRuleDefinition.noClasses().should(haveAnyDependenciesThat(resideInSamePackage()))
                .because("because");
        EvaluationResult result = rule.evaluate(scanClasses(JavaClassTest.SameUsage.class));
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void test_other() {
        final var rule = ArchRuleDefinition.noClasses().should(haveAnyDependenciesThat(resideInSamePackage()))
                .because("because");
        EvaluationResult result = rule.evaluate(scanClasses(JavaClassTest.OtherUsage.class));
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void test_resideInPackageAnnotatedWith_true() {
        assertThat(JavaClass.Predicates.resideInPackageAnnotatedWith(Deprecated.class)
                .test(scanClassesWithPackage(ClassInDeprecatedPackage.class).get(ClassInDeprecatedPackage.class)))
                .isTrue();
    }

    @Test
    public void test_resideInPackageThat_message() {
        final var rule = ArchRuleDefinition.noClasses()
                .should(ArchCondition.from((resideInAPackageThat(alwaysTrue()))))
                .because("because");
        EvaluationResult result = rule.evaluate(scanClasses(ClassInDeprecatedPackage.class));
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("no classes should reside in a package that");
    }

    @Test
    public void test_resideInPackageAnnotatedWith_false() {
        assertThat(JavaClass.Predicates.resideInPackageAnnotatedWith(Deprecated.class)
                .test(scanClassesWithPackage(ClassInOtherPackage.class).get(ClassInOtherPackage.class)))
                .isFalse();
    }

    @Test
    public void test_resideInAPackageThat_true() {
        assertThat(resideInAPackageThat(name("com.netflix.nebula.archrules.common.other"))
                .test(scanClass(ClassInOtherPackage.class)))
                .isTrue();
    }

    @Test
    public void test_resideInAPackageThat_false() {
        assertThat(resideInAPackageThat(name("com.netflix.nebula.archrules.common"))
                .test(scanClass(ClassInOtherPackage.class)))
                .isFalse();
    }

    @SuppressWarnings("unused")
    static class OtherUsage {
        ClassInOtherPackage otherPackage;
    }

    @SuppressWarnings("unused")
    static class SameUsage {
        DependencyTest.OtherUsage samePackage;
    }
}
