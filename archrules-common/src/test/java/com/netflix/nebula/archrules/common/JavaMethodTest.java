package com.netflix.nebula.archrules.common;

import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.common.Util.scanClass;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaMethodTest {

    @Test
    public void test_getters_get() {
        assertThat(JavaMethod.Predicates.aGetter().test(scanClass(AClass.class).getMethod("get")))
                .isFalse();
    }

    @Test
    public void test_getters_getThing() {
        assertThat(JavaMethod.Predicates.aGetter().test(scanClass(AClass.class).getMethod("getThing")))
                .isTrue();
    }

    static class AClass {
        @SuppressWarnings("unused")
        public String get() {
            return "";
        }

        @SuppressWarnings("unused")
        public String getThing() {
            return "";
        }
    }
}
