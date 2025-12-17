package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.gradle.FakeDeprecatedGradleClass;
import org.gradle.FakeDeprecatedGradleMethod;
import org.gradle.internal.InternalGradleClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PredicatesTest {
    private static JavaClass scan(Class<?> clazz) {
        return new ClassFileImporter().importClass(clazz);
    }

    @Test
    public void test_gradleClass() {
        assertThat(Predicates.gradleClass.test(scan(FakeDeprecatedGradleMethod.class))).isTrue();
    }

    @Test
    public void test_deprecatedGradleClass() {
        assertThat(Predicates.deprecatedGradleClass.test(scan(FakeDeprecatedGradleMethod.class))).isFalse();
        assertThat(Predicates.deprecatedGradleClass.test(scan(FakeDeprecatedGradleClass.class))).isTrue();
    }

    @Test
    public void test_internalGradleClass() {
        assertThat(Predicates.internalGradleClass.test(scan(FakeDeprecatedGradleMethod.class))).isFalse();
        assertThat(Predicates.internalGradleClass.test(scan(InternalGradleClass.class))).isTrue();
    }

    @Test
    public void test_annotatedWithFileInputAnnotation() {
        assertThat(Predicates.areAnnotatedWithFileInputAnnotation.test(
                scan(GradleTaskCacheabilityRuleTest.CacheableTaskWithoutPathSensitive.class).getField("inputFile"))
        ).isTrue();
    }

    @Test
    public void test_getters_get() {
        assertThat(Predicates.getters.test(scan(AClass.class).getMethod("get"))).isFalse();
    }


    static class AClass {
        @SuppressWarnings("unused")
        public String get() {
            return "";
        }
    }
}
