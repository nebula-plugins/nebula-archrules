package com.netflix.nebula.archrules.nullability;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NebulaNullabilityArchRulesTest {
    @Test
    public void test() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.PUBLIC_CLASSES_SHOULD_BE_NULL_MARKED,
                FailingClass.class);
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void test_inner() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.PUBLIC_CLASSES_SHOULD_BE_NULL_MARKED,
                InnerClass.class);
        assertThat(result.hasViolation())
                .as("Inner classes do not need to be null-marked")
                .isFalse();
    }

    @Test
    public void test_null_marked() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.PUBLIC_CLASSES_SHOULD_BE_NULL_MARKED,
                PassingClass.class);
        assertThat(result.hasViolation())
                .as("NullMarked classes pass")
                .isFalse();
    }

    @Test
    public void test_test() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.PUBLIC_CLASSES_SHOULD_BE_NULL_MARKED,
                NebulaNullabilityArchRulesTest.class);
        assertThat(result.hasViolation())
                .as("test classes pass")
                .isFalse();
    }

    @Test
    public void test_javax() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.UPGRADE_LEGACY_JAVAX,
                JavaxFailingClass.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(2);
    }

    @Test
    public void test_jakarta() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.UPGRADE_LEGACY_JAKARTA,
                JakartaFailingClass.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(2);
    }

    @Test
    public void test_jetbrains() {
        EvaluationResult result = Runner.check(
                NebulaNullabilityArchRules.UPGRADE_LEGACY_JETBRAINS,
                JetbrainsFailingClass.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(2);
    }

    @Test
    public void test_spring_framework() {
      EvaluationResult result = Runner.check(
          NebulaNullabilityArchRules.UPGRADE_LEGACY_SPRING_FRAMEWORK,
          SpringFrameworkFailingClass.class);
      assertThat(result.hasViolation())
          .isTrue();
      assertThat(result.getFailureReport().getDetails())
          .hasSize(2);
  }

    static class InnerClass {
    }
}
