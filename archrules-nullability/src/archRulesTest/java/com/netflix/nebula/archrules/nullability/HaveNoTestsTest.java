package com.netflix.nebula.archrules.nullability;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.netflix.nebula.archrules.nullability.HaveNoTests.haveNoTests;
import static org.assertj.core.api.Assertions.assertThat;

class HaveNoTestsTest {
    @Test
    public void test_jupiter_test() {
        JavaClass codeToTest = new ClassFileImporter().importClass(UsesJupiterTest.class);
        HaveNoTests instance = haveNoTests();
        assertThat(instance.test(codeToTest)).isFalse();
    }

    @Test
    public void test_jupiter_parameterized_test() {
        JavaClass codeToTest = new ClassFileImporter().importClass(UsesJupiterParameterizedTest.class);
        HaveNoTests instance = haveNoTests();
        assertThat(instance.test(codeToTest)).isFalse();
    }

    @Test
    public void test_nothing() {
        JavaClass codeToTest = new ClassFileImporter().importClass(UsesNothing.class);
        HaveNoTests instance = haveNoTests();
        assertThat(instance.test(codeToTest)).isTrue();
    }

    static class UsesJupiterTest {
        @Test
        void test() {

        }
    }

    static class UsesJupiterParameterizedTest {
        @ParameterizedTest
        @ValueSource(strings = {"a", "b"})
        void test(String param) {

        }
    }

    static class UsesNothing {
        void test() {

        }
    }
}
