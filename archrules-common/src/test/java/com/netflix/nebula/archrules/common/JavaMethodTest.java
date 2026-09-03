package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.examples.InternalKotlinTopLevelFunctionKt;
import com.netflix.nebula.archrules.common.examples.PublicJavaClass;
import com.netflix.nebula.archrules.common.examples.PublicKotlinClass;
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

    @Test
    public void test_kotlinInternal_java_public() {
        assertThat(
                JavaMethod.Predicates.kotlinInternal()
                        .test(Util.scanClass(PublicJavaClass.class).getMethod("publicJavaMethod"))
        ).isFalse();
    }


    @Test
    public void test_kotlinInternal_internal_top_level_function() {
        assertThat(
                JavaMethod.Predicates.kotlinInternal()
                        .test(scanClass(InternalKotlinTopLevelFunctionKt.class).getMethod("internalKotlinTopLevelFunction"))
        ).isTrue();
    }

    @Test
    public void test_kotlinInternal_public() {
        assertThat(
                JavaMethod.Predicates.kotlinInternal()
                        .test(Util.scanClass(PublicKotlinClass.class).getMethod("publicMethod"))
        ).isFalse();
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
