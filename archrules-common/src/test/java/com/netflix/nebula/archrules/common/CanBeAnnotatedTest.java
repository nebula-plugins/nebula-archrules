package com.netflix.nebula.archrules.common;

import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.common.Util.scanClass;
import static org.assertj.core.api.Assertions.assertThat;

class CanBeAnnotatedTest {

    @Test
    public void test_javaDeprecatedClass() {
        assertThat(CanBeAnnotated.Predicates.deprecated().test(scanClass(Usage.class))).isFalse();
        assertThat(CanBeAnnotated.Predicates.deprecated().getDescription()).isEqualTo("deprecated");
    }

    @Test
    public void test_javaDeprecatedForRemovalClass() {
        assertThat(CanBeAnnotated.Predicates.deprecatedForRemoval().test(scanClass(Usage.class))).isFalse();
        assertThat(CanBeAnnotated.Predicates.deprecatedForRemoval().getDescription()).isEqualTo("deprecated for removal");
    }

    @Deprecated
    static class JavaDeprecatedClass {

    }

    @Deprecated(forRemoval = true)
    static class JavaDeprecatedForRemovalClass {

    }

    static class Usage {
        JavaDeprecatedClass aField;
        JavaDeprecatedForRemovalClass deprecatedForRemoval;
    }
}
