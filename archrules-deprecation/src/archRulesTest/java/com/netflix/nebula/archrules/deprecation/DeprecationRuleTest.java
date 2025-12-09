package com.netflix.nebula.archrules.deprecation;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.Priority;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DeprecationRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(DeprecationRuleTest.class);

    @Test
    public void testDeprecationRule_class() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, CodeThatUsesDeprecatedClass.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
    }

    @Test
    public void testDeprecationRule_method() {
        final EvaluationResult result = Runner.check(DeprecationRule.deprecationRule, CodeThatUsesDeprecatedMethod.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getPriority()).isEqualTo(Priority.LOW);
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

    static class MethodThatIsDeprecated {
        @Deprecated
        static void deprecated() {

        }
    }

    static class CodeThatUsesDeprecatedMethod {
        static void usage() {
            MethodThatIsDeprecated.deprecated();
        }
    }

    static class CodeThatUsesDeprecatedClass {
        static void usage() {
            ClassIsDeprecated.deprecated();
        }
    }

    @Deprecated
    interface DeprecatedInterface {
        void notDeprecated();
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
}
