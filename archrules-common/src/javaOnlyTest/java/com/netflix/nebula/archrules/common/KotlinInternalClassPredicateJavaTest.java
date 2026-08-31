package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.examples.PublicJavaClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KotlinInternalClassPredicateJavaTest {
    @Test
     void test_kotlinInternal_public() {
        assertThat(
           new KotlinInternalClassPredicate().test(Util.scanClass(PublicJavaClass.class))
        ).isFalse();
    }
}
