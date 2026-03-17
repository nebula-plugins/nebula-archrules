package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.DefaultTask;
import org.gradle.api.internal.provider.DefaultProvider;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskAbstractGetterRuleTest {

    @Test
    public void test_abstractGetters_fail() {
        final EvaluationResult result = Runner.check(
                TaskAbstractGetterRule.RULE,
                TaskWithConcreteGetter.class
        );
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("methods that are task property getters should have modifier ABSTRACT")
                .contains("because task implementations should define properties as abstract getters");
    }

    @Test
    public void test_abstractGetters_pass() {
        final EvaluationResult result = Runner.check(
                TaskAbstractGetterRule.RULE,
                TaskWithAbstractGetter.class
        );
        assertThat(result.hasViolation())
                .as(result.getFailureReport().toString())
                .isFalse();
    }

    public static abstract class TaskWithAbstractGetter extends DefaultTask {

        @Input
        public abstract Provider<String> getMessage();

        @TaskAction
        public void execute() {
            System.out.println(" ");
        }
    }

    public static abstract class TaskWithConcreteGetter extends DefaultTask {
        public String message;

        @Input
        public Provider<String> getMessage() {
            return new DefaultProvider<>(() -> message);
        }

        @TaskAction
        public void execute() {
            System.out.println(message);
        }
    }
}
