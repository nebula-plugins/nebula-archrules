package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.conditions.NebulaAnyDependencyCondition;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;

import static com.tngtech.archunit.core.domain.JavaClass.Functions.GET_DIRECT_DEPENDENCIES_FROM_SELF;
import static com.tngtech.archunit.core.domain.JavaClass.Functions.GET_PACKAGE;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.is;

@NullMarked
public class JavaClass {
    public static class Predicates {
        /**
         * evaluates the predicate on the class's package
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.JavaClass> resideInAPackageThat(
                DescribedPredicate<? super JavaPackage> condition) {
            return condition.onResultOf(GET_PACKAGE)
                    .as("reside in a package that %s", condition.getDescription());
        }

        /**
         * checks if the class's package is annotated with a specific annotation
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.JavaClass> resideInPackageAnnotatedWith(
                Class<? extends Annotation> annotationClass
        ) {
            return resideInAPackageThat(is(annotatedWith(annotationClass)));
        }
    }

    public static class Conditions {
        /**
         * Can be removed once <a href="https://github.com/TNG/ArchUnit/pull/1580">haveAnyDependenciesThat</a> is merged.
         */
        @Deprecated
        public static ArchCondition<com.tngtech.archunit.core.domain.JavaClass> haveAnyDependenciesThat(
                DescribedPredicate<? super Dependency> predicate) {
            return new NebulaAnyDependencyCondition(
                    "have any dependencies that " + predicate.getDescription(),
                    predicate,
                    GET_DIRECT_DEPENDENCIES_FROM_SELF);
        }
    }
}
