package com.netflix.nebula.archrules.guava;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class GuavaRulesTest {
    @Test
    public void test_optional_pass() {
        EvaluationResult result = Runner.check(GuavaRules.OPTIONAL, OptionalPass.class);
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void test_optional_fail() {
        EvaluationResult result = Runner.check(GuavaRules.OPTIONAL, OptionalFail.class);
        assertThat(result.hasViolation()).isTrue();
    }

    @Test
    public void test_collections_pass() {
        EvaluationResult result = Runner.check(GuavaRules.COLLECTIONS, CollectionsPass.class);
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void test_collections_fail() {
        EvaluationResult result = Runner.check(GuavaRules.COLLECTIONS, CollectionsFail.class);
        assertThat(result.hasViolation()).isTrue();
    }


    static class OptionalFail {
        com.google.common.base.Optional<String> get() {
            return com.google.common.base.Optional.absent();
        }
    }

    static class OptionalPass {
        java.util.Optional<String> get() {
            return java.util.Optional.empty();
        }
    }

    static class CollectionsFail {
        java.util.List<String> get() {
            return com.google.common.collect.Lists.newArrayList("");
        }
    }

    static class CollectionsPass {
        java.util.List<String> get() {
            return Collections.singletonList("");
        }
    }
}
