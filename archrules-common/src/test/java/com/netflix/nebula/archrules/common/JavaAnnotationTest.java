package com.netflix.nebula.archrules.common;

import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.common.Util.scanClass;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaAnnotationTest {
    @Test
    public void test() {
        assertThat(JavaAnnotation.Predicates.deprecatedForRemoval()
                .test(scanClass(DeprecatedForRemovalClass.class).getAnnotationOfType("java.lang.Deprecated")))
                .isTrue();
    }

    @Deprecated(forRemoval = true)
    static class DeprecatedForRemovalClass {

    }
}

