package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.ChainableFunction;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.core.domain.properties.HasReturnType;
import com.tngtech.archunit.lang.conditions.ArchPredicates;

import java.util.Set;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.containAnyMethodsThat;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasReturnType.Predicates.rawReturnType;
import static com.tngtech.archunit.core.domain.properties.HasType.Predicates.rawType;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.has;

class Predicates {
    static final DescribedPredicate<JavaMethod> getters = new DescribedPredicate<JavaMethod>("getters") {
        @Override
        public boolean test(JavaMethod input) {
            if (input.getModifiers().contains(JavaModifier.STATIC)) {
                return false;
            }
            if (!input.getParameters().isEmpty()) {
                return false;
            }
            if (input.getName().startsWith("get")) {
                if (Character.isLowerCase(input.getName().charAt(3))) {
                    return false;
                }
                return true;
            }
            if (input.getName().startsWith("is")) {
                if (input.getRawReturnType().isAssignableTo(Boolean.class)) {
                    return false;
                }
                if (Character.isLowerCase(input.getName().charAt(2))) {
                    return false;
                }
                return true;
            }
            return false;
        }
    };

    static final DescribedPredicate<HasReturnType> hasRichPropertyReturnType = ArchPredicates
            .has(rawReturnType(assignableTo("org.gradle.api.provider.Provider")
                    .or(assignableTo("org.gradle.api.file.FileCollection"))))
            .as("has rich property return type");

    static final DescribedPredicate<JavaAccess<?>> taskIsCreatedEagerly = ArchPredicates
            .is(target(has(name("task").or(name("create")))))
            .and(targetOwner(assignableTo("org.gradle.api.Project"))
                    .or(targetOwner(assignableTo("org.gradle.api.tasks.TaskContainer"))))
            .as("task is created eagerly");

    static final DescribedPredicate<JavaClass> gradleClass = ArchPredicates.is(resideInAPackage("org.gradle.."));
    static final DescribedPredicate<JavaClass> deprecatedGradleClass = ArchPredicates.is(gradleClass).and(annotatedWith(Deprecated.class));
    static final DescribedPredicate<JavaClass> internalGradleClass = ArchPredicates.is(gradleClass).and(resideInAPackage("..internal.."));

    static final DescribedPredicate<JavaAccess<?>> accessDeprecatedGradleApi = ArchPredicates
            .is(targetOwner(deprecatedGradleClass))
            .or(target(annotatedWith(Deprecated.class)));

    static final DescribedPredicate<JavaClass> haveTaskAction =
            ArchPredicates.have(containAnyMethodsThat(are(annotatedWith("org.gradle.api.tasks.TaskAction"))));

    static DescribedPredicate<CanBeAnnotated> annotatedWithAny(Set<String> annotationClasses) {
        return annotationClasses.stream()
                .map(CanBeAnnotated.Predicates::annotatedWith)
                .reduce((a, b) -> a.or(b))
                .orElseGet(() -> annotatedWith(rawType(assignableTo(Object.class))))
                .as("annotated with any [%s]", String.join(", ", annotationClasses));
    }

    static DescribedPredicate<JavaClass> containAnyMethodsInClassHierarchyThat(DescribedPredicate<? super JavaMethod> predicate) {
        return new ContainAnyMembersInClassHierarchyThatPredicate<>("methods", GET_ALL_METHODS, predicate);
    }

    static DescribedPredicate<JavaClass> containAnyFieldsInClassHierarchyThat(DescribedPredicate<? super JavaField> predicate) {
        return new ContainAnyMembersInClassHierarchyThatPredicate<>("fields", GET_ALL_FIELDS, predicate);
    }

    static final ChainableFunction<JavaClass, Set<JavaMethod>> GET_ALL_METHODS =
            new ChainableFunction<JavaClass, Set<JavaMethod>>() {
                @Override
                public Set<JavaMethod> apply(JavaClass input) {
                    return input.getAllMethods();
                }
            };

    static final ChainableFunction<JavaClass, Set<JavaField>> GET_ALL_FIELDS =
            new ChainableFunction<JavaClass, Set<JavaField>>() {
                @Override
                public Set<JavaField> apply(JavaClass input) {
                    return input.getAllFields();
                }
            };

    private static final String ANNOTATION_INPUT_FILE = "org.gradle.api.tasks.InputFile";
    private static final String ANNOTATION_INPUT_FILES = "org.gradle.api.tasks.InputFiles";
    private static final String ANNOTATION_INPUT_DIRECTORY = "org.gradle.api.tasks.InputDirectory";
    static final DescribedPredicate<CanBeAnnotated> areAnnotatedWithFileInputAnnotation =
            ArchPredicates.are(annotatedWith(ANNOTATION_INPUT_FILE))
                    .or(annotatedWith(ANNOTATION_INPUT_FILES))
                    .or(annotatedWith(ANNOTATION_INPUT_DIRECTORY))
                    .as("annotated with Input file annotations");
}
