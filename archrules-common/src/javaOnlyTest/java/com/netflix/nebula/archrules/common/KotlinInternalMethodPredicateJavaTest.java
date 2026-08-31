package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.examples.PublicJavaClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KotlinInternalMethodPredicateJavaTest {
    @Test
    void test_kotlinInternal_public() {
        assertThat(
                new KotlinInternalMethodPredicate()
                        .test(Util.scanClass(PublicJavaClass.class).getMethod("publicJavaMethod"))
        ).isFalse();
    }
}
