package com.netflix.nebula.archrules.spring;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

class NoFieldInjectionRuleTest {
    @Test
    public void test_javax_field() {
        EvaluationResult result = Runner.check(NoFieldInjectionRule.RULE, JavaxFieldInjection.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("no fields should be annotated with @Inject or @Autowired")
                .contains("constructor injection is preferred over field injection");
    }

    @Test
    public void test_jakarta_field() {
        EvaluationResult result = Runner.check(NoFieldInjectionRule.RULE, JakartaFieldInjection.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("no fields should be annotated with @Inject or @Autowired")
                .contains("constructor injection is preferred over field injection");
    }

    @Test
    public void test_spring_field() {
        EvaluationResult result = Runner.check(NoFieldInjectionRule.RULE, SpringFieldInjection.class);
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString())
                .contains("no fields should be annotated with @Inject or @Autowired")
                .contains("constructor injection is preferred over field injection");
    }

    static class JavaxFieldInjection {
        @javax.inject.Inject
        String field;
    }

    static class JakartaFieldInjection {
        @jakarta.inject.Inject
        String field;
    }

    @Component
    static class SpringFieldInjection {
        @Autowired
        String field;
    }
}
