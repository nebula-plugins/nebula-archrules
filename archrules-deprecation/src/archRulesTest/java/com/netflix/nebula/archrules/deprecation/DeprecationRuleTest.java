package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.Runner;
import com.netflix.nebula.archrules.deprecation.other.ClassThatIsJavaDeprecated;
import com.netflix.nebula.archrules.deprecation.other.DeprecatedForRemovalClass;
import com.netflix.nebula.archrules.deprecation.other.DeprecatedInterface;
import com.netflix.nebula.archrules.deprecation.other.MethodThatIsDeprecated;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.Priority;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DeprecationRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(DeprecationRuleTest.class);

    static final String CLASS_DEPRECATED = "have any dependencies that do not reside in same package and target is deprecated";
    static final String ACCESS_TARGET_PACKAGE = "should access target where not in the same package";
    static final String TARGET_IS_DEPRECATED = "target is deprecated";
    static final String TARGET_OWNER_IS_DEPRECATED = "target owner is deprecated";

    @Test
    public void testDeprecationRule_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, CodeThatUsesJavaDeprecatedClass.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
        assertThat(result.getFailureReport().toString())
                .contains("no classes should " + CLASS_DEPRECATED + " " +
                          "or " + ACCESS_TARGET_PACKAGE + " and " + TARGET_IS_DEPRECATED + " or ");

        assertThat(result.getFailureReport().toString())
                .as("buggy behavior that will be fixed in https://github.com/TNG/ArchUnit/pull/1579")
                .doesNotContain(TARGET_OWNER_IS_DEPRECATED);
    }

    @Test
    public void testDeprecationRule_method_samePackage() {
        final EvaluationResult result =
                Runner.check(DeprecationRule.deprecationRule, CodeThatUsesDeprecatedMethodSamePackage.class);
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void testDeprecationRule_method_differentPackage() {
        final EvaluationResult result =
                Runner.check(DeprecationRule.deprecationRule, CodeThatUsesDeprecatedMethodOtherPackage.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
        assertThat(result.getFailureReport().toString())
                .contains("no classes should " + CLASS_DEPRECATED + " " +
                          "or " + ACCESS_TARGET_PACKAGE + " and " + TARGET_IS_DEPRECATED);

        assertThat(result.getFailureReport().toString())
                .as("buggy behavior that will be fixed in https://github.com/TNG/ArchUnit/pull/1579")
                .doesNotContain("or target owner is deprecated");
    }

    @Test
    public void testDeprecationRule_interface() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, CodeThatUsesDeprecatedInterface.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
    }

    @Test
    public void testDeprecationRule_impl_interface() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, Impl.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
    }

    @Test
    public void testDeprecationRule_extends_interface() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, CodeThatUsesClassThatExtendsDeprecatedInterface.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation())
                .as("Don't flag usage of a non-deprecated class that extends a deprecated class")
                .isFalse();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
    }

    @Deprecated
    static class ClassIsDeprecated {
        static void deprecated() {
        }
    }

    static class MethodThatIsDeprecatedSamePackage {
        @Deprecated
        static void deprecated() {

        }
    }

    static class CodeThatUsesDeprecatedMethodSamePackage {
        static void usage() {
            MethodThatIsDeprecatedSamePackage.deprecated();
        }
    }

    static class CodeThatUsesDeprecatedMethodOtherPackage {
        static void usage() {
            MethodThatIsDeprecated.deprecated();
        }
    }



    static class Impl implements DeprecatedInterface {
        public void notDeprecated() {
        }
    }

    static class CodeThatUsesDeprecatedInterface {
        static void usage(DeprecatedInterface impl) {
            impl.notDeprecated();
        }
    }

    static class CodeThatUsesClassThatExtendsDeprecatedInterface {
        static void usage(Impl impl) {
            impl.notDeprecated();
        }
    }

    static class CodeThatUsesJavaDeprecatedClass {
        ClassThatIsJavaDeprecated javaDeprecated;
    }
}
