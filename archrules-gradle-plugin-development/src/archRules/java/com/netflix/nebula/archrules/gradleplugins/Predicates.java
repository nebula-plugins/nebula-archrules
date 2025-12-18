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

    /**
     * Matches getter methods (getX(), isX()) that follow JavaBeans naming conventions.
     */
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
                if (input.getName().length() < 4 || Character.isLowerCase(input.getName().charAt(3))) {
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

    /**
     * Matches methods returning Provider API or FileCollection types.
     */
    static final DescribedPredicate<HasReturnType> hasRichPropertyReturnType = ArchPredicates
            .has(rawReturnType(assignableTo("org.gradle.api.provider.Provider")
                    .or(assignableTo("org.gradle.api.file.FileCollection"))))
            .as("has rich property return type");

    /**
     * Matches calls to task() or create() methods that eagerly create tasks.
     */
    static final DescribedPredicate<JavaAccess<?>> taskIsCreatedEagerly = ArchPredicates
            .is(target(has(name("task").or(name("create")))))
            .and(targetOwner(assignableTo("org.gradle.api.Project"))
                    .or(targetOwner(assignableTo("org.gradle.api.tasks.TaskContainer"))))
            .as("task is created eagerly");

    /** Matches classes in the org.gradle package. */
    static final DescribedPredicate<JavaClass> gradleClass = ArchPredicates.is(resideInAPackage("org.gradle.."));

    /** Matches deprecated Gradle API classes. */
    static final DescribedPredicate<JavaClass> deprecatedGradleClass = ArchPredicates.is(gradleClass).and(annotatedWith(Deprecated.class));

    /** Matches Gradle internal API classes (in ..internal.. packages). */
    static final DescribedPredicate<JavaClass> internalGradleClass = ArchPredicates.is(gradleClass).and(resideInAPackage("..internal.."));

    /** Matches calls to deprecated Gradle APIs. */
    static final DescribedPredicate<JavaAccess<?>> accessDeprecatedGradleApi = ArchPredicates
            .is(targetOwner(deprecatedGradleClass))
            .or(target(annotatedWith(Deprecated.class)));

    /** Matches classes that have at least one @TaskAction method. */
    static final DescribedPredicate<JavaClass> haveTaskAction =
            ArchPredicates.have(containAnyMethodsThat(are(annotatedWith("org.gradle.api.tasks.TaskAction"))));

    /** Creates a predicate matching elements annotated with any of the given annotations. */
    static DescribedPredicate<CanBeAnnotated> annotatedWithAny(Set<String> annotationClasses) {
        return annotationClasses.stream()
                .map(CanBeAnnotated.Predicates::annotatedWith)
                .reduce((a, b) -> a.or(b))
                .orElseGet(() -> annotatedWith(rawType(assignableTo(Object.class))))
                .as("annotated with any [%s]", String.join(", ", annotationClasses));
    }

    /** Matches classes with at least one method in their hierarchy satisfying the predicate. */
    static DescribedPredicate<JavaClass> containAnyMethodsInClassHierarchyThat(DescribedPredicate<? super JavaMethod> predicate) {
        return new ContainAnyMembersInClassHierarchyThatPredicate<>("methods", GET_ALL_METHODS, predicate);
    }

    /** Matches classes with at least one field in their hierarchy satisfying the predicate. */
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

    /** Matches elements annotated with @InputFile, @InputFiles, or @InputDirectory. */
    static final DescribedPredicate<CanBeAnnotated> areAnnotatedWithFileInputAnnotation =
            ArchPredicates.are(annotatedWith(ANNOTATION_INPUT_FILE))
                    .or(annotatedWith(ANNOTATION_INPUT_FILES))
                    .or(annotatedWith(ANNOTATION_INPUT_DIRECTORY))
                    .as("annotated with Input file annotations");

    /** Returns true if the type is a Gradle Provider API type (Property, Provider, FileCollection, etc.). */
    static boolean isProviderApiType(JavaClass type) {
        return type.isAssignableTo("org.gradle.api.provider.Property") ||
               type.isAssignableTo("org.gradle.api.provider.Provider") ||
               type.isAssignableTo("org.gradle.api.provider.ListProperty") ||
               type.isAssignableTo("org.gradle.api.provider.SetProperty") ||
               type.isAssignableTo("org.gradle.api.provider.MapProperty") ||
               type.isAssignableTo("org.gradle.api.file.RegularFileProperty") ||
               type.isAssignableTo("org.gradle.api.file.DirectoryProperty") ||
               type.isAssignableTo("org.gradle.api.file.ConfigurableFileCollection") ||
               type.isAssignableTo("org.gradle.api.file.FileCollection");
    }

    /** Creates a predicate matching calls to the specified method on the given owner class. */
    static DescribedPredicate<JavaAccess<?>> callsMethodOn(String methodName, String ownerClass) {
        return ArchPredicates.are(target(name(methodName)))
                .and(targetOwner(assignableTo(ownerClass)))
                .as("calls " + methodName + " on " + ownerClass);
    }

    /** Creates a predicate matching calls to the specified method on any of the given owner classes. */
    static DescribedPredicate<JavaAccess<?>> callsMethodOnAny(String methodName, String... ownerClasses) {
        DescribedPredicate<JavaAccess<?>> predicate = are(target(name(methodName)));
        DescribedPredicate<JavaClass> ownerPredicate = null;

        for (String ownerClass : ownerClasses) {
            DescribedPredicate<JavaClass> current = assignableTo(ownerClass);
            ownerPredicate = (ownerPredicate == null) ? current : ownerPredicate.or(current);
        }

        return predicate.and(targetOwner(ownerPredicate))
                .as("calls " + methodName + " on any of " + String.join(", ", ownerClasses));
    }
}
