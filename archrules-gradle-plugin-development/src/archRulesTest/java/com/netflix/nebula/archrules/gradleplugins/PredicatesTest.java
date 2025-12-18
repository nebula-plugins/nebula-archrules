package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.gradle.FakeDeprecatedGradleClass;
import org.gradle.FakeDeprecatedGradleMethod;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.internal.InternalGradleClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

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

    static Stream<Class<?>> providerApiTypes() {
        return Stream.of(
                Property.class,
                Provider.class,
                ListProperty.class,
                SetProperty.class,
                MapProperty.class,
                RegularFileProperty.class,
                DirectoryProperty.class,
                ConfigurableFileCollection.class,
                FileCollection.class
        );
    }

    @ParameterizedTest
    @MethodSource("providerApiTypes")
    public void test_isProviderApiType_shouldReturnTrue(Class<?> type) {
        assertThat(Predicates.isProviderApiType(scan(type))).isTrue();
    }

    static Stream<Class<?>> nonProviderApiTypes() {
        return Stream.of(String.class, Integer.class);
    }

    @ParameterizedTest
    @MethodSource("nonProviderApiTypes")
    public void test_isProviderApiType_shouldReturnFalse(Class<?> type) {
        assertThat(Predicates.isProviderApiType(scan(type))).isFalse();
    }

    @Test
    public void test_callsMethodOn_shouldMatchCorrectMethodCall() {
        JavaClasses classes = new ClassFileImporter().importClasses(ClassCallingGetObjects.class, Project.class);
        Set<JavaAccess<?>> accesses = classes.get(ClassCallingGetObjects.class).getAccessesFromSelf();

        long matchCount = accesses.stream()
                .filter(access -> Predicates.callsMethodOn("getObjects", "org.gradle.api.Project").test(access))
                .count();

        assertThat(matchCount).isEqualTo(1);
    }

    @Test
    public void test_callsMethodOn_shouldNotMatchDifferentMethod() {
        JavaClasses classes = new ClassFileImporter().importClasses(ClassCallingGetObjects.class, Project.class);
        Set<JavaAccess<?>> accesses = classes.get(ClassCallingGetObjects.class).getAccessesFromSelf();

        long matchCount = accesses.stream()
                .filter(access -> Predicates.callsMethodOn("getProviders", "org.gradle.api.Project").test(access))
                .count();

        assertThat(matchCount).isEqualTo(0);
    }

    @Test
    public void test_callsMethodOnAny_shouldMatchCallOnFirstOwner() {
        JavaClasses classes = new ClassFileImporter().importClasses(ClassCallingGetByName.class, Project.class, TaskContainer.class);
        Set<JavaAccess<?>> accesses = classes.get(ClassCallingGetByName.class).getAccessesFromSelf();

        long matchCount = accesses.stream()
                .filter(access -> Predicates.callsMethodOnAny("getByName", "org.gradle.api.tasks.TaskContainer", "org.gradle.api.tasks.TaskCollection").test(access))
                .count();

        assertThat(matchCount).isEqualTo(1);
    }

    @Test
    public void test_callsMethodOnAny_shouldNotMatchDifferentOwner() {
        JavaClasses classes = new ClassFileImporter().importClasses(ClassCallingGetByName.class, Project.class, TaskContainer.class);
        Set<JavaAccess<?>> accesses = classes.get(ClassCallingGetByName.class).getAccessesFromSelf();

        long matchCount = accesses.stream()
                .filter(access -> Predicates.callsMethodOnAny("getByName", "org.gradle.api.Project").test(access))
                .count();

        assertThat(matchCount).isEqualTo(0);
    }


    static class AClass {
        @SuppressWarnings("unused")
        public String get() {
            return "";
        }
    }

    @SuppressWarnings("unused")
    static class ClassCallingGetObjects {
        public void method(Project project) {
            project.getObjects();
        }
    }

    @SuppressWarnings("unused")
    static class ClassCallingGetByName {
        public void method(Project project) {
            project.getTasks().getByName("test");
        }
    }
}
