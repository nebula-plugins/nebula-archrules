package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.Runner;
import com.netflix.nebula.archrules.deprecation.other.DeprecatedForRemovalClass;
import com.netflix.nebula.archrules.deprecation.other.DeprecatedForRemovalMethod;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.Priority;
import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.deprecation.DeprecationRuleTest.ACCESS_TARGET_PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

public class DeprecationForRemovalRuleTest {
    private static final String CLASS_DEPRECATED_FOR_REMOVAL = "have any dependencies that do not reside in same package and target is deprecated for removal";
    private static final String TARGET_IS_DEPRECATED_FOR_REMOVAL = "target is deprecated for removal";
    private static final String TARGET_OWNER_IS_DEPRECATED_FOR_REMOVAL = "target owner is deprecated for removal";

    @Test
    public void testDeprecationForRemovalRule_method() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, CodeThatUsesDeprecatedForRemovalMethod.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    public void testDeprecationForRemovalRule_method_samePackage() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, DeprecationForRemovalRuleTest.CodeThatUsesDeprecatedForRemovalMethodSamePackage.class);
        assertThat(result.hasViolation()).isFalse();
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    public void testRegularDeprecated() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, DeprecationForRemovalRuleTest.CodeThatUsesRegularDeprecatedClass.class);
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void testDeprecatedForRemovalRule_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationForRemovalRule, CodeThatUsesDeprecatedForRemovalClass.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(result.getFailureReport().toString())
                .contains("no classes should " + CLASS_DEPRECATED_FOR_REMOVAL +
                          " or " + ACCESS_TARGET_PACKAGE + " and " + TARGET_IS_DEPRECATED_FOR_REMOVAL + " or ");

        assertThat(result.getFailureReport().toString())
                .as("buggy behavior that will be fixed in https://github.com/TNG/ArchUnit/pull/1579")
                .doesNotContain(TARGET_OWNER_IS_DEPRECATED_FOR_REMOVAL);
    }

    @Test
    public void testDeprecatedForRemovalRule_class_samePackage() {
        final EvaluationResult result = Runner.check(
                DeprecationRule.deprecationForRemovalRule, CodeThatUsesDeprecatedForRemovalClassSamePackage.class);
        assertThat(result.hasViolation()).isFalse();
    }

    static class DeprecatedForRemovalMethodSamePackage {
        @Deprecated(forRemoval = true)
        static void deprecated() {}
    }

    @Deprecated
    static class RegularDeprecatedClass {
        static void method() {}
    }

    static class CodeThatUsesDeprecatedForRemovalMethod {
        static void usage() {
            DeprecatedForRemovalMethod.deprecated();
        }
    }

    static class CodeThatUsesDeprecatedForRemovalMethodSamePackage {
        static void usage() {
            DeprecatedForRemovalMethodSamePackage.deprecated();
        }
    }

    static class CodeThatUsesRegularDeprecatedClass {
        static void usage() {
            RegularDeprecatedClass.method();
        }
    }

    @Deprecated(forRemoval = true)
    static class DeprecatedForRemovalClassSamePackage {
    }

    static class CodeThatUsesDeprecatedForRemovalClass {
        DeprecatedForRemovalClass javaDeprecated;
    }

    static class CodeThatUsesDeprecatedForRemovalClassSamePackage {
        DeprecatedForRemovalClassSamePackage javaDeprecated;
    }
}
