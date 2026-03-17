package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasType.Predicates.rawType;

@NullMarked
public class CanBeAnnotated {
    public static class Predicates {

        /**
         * Annotated with Java or Kotlin deprecation annotations
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.properties.CanBeAnnotated> deprecated() {
            return annotatedWith(Deprecated.class)
                    .or(annotatedWith("kotlin.Deprecated"))
                    .or(annotatedWith("kotlin.DeprecatedSinceKotlin"))
                    .as("deprecated");
        }

        /**
         * Annotated with Java deprecation annotation with the forRemoval property set to true
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.properties.CanBeAnnotated> deprecatedForRemoval() {
            return annotatedWith(JavaAnnotation.Predicates.deprecatedForRemoval())
                    .as("deprecated for removal");
        }

        /**
         * Creates a predicate matching elements annotated with any of the given annotations.
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.properties.CanBeAnnotated> annotatedWithAny(
                String... annotationTypes) {
            return Arrays.stream(annotationTypes)
                    .map(com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates::annotatedWith)
                    .reduce((a, b) -> a.or(b))
                    .orElseGet(() -> annotatedWith(rawType(assignableTo(Object.class))))
                    .as("annotated with any [%s]", String.join(", ", annotationTypes));
        }

        /**
         * Creates a predicate matching elements annotated with any of the given annotations.
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.properties.CanBeAnnotated> annotatedWithAny(
                Iterable<String> annotationTypes) {
            return StreamSupport.stream(annotationTypes.spliterator(), false)
                    .map(com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates::annotatedWith)
                    .reduce((a, b) -> a.or(b))
                    .orElseGet(() -> annotatedWith(rawType(assignableTo(Object.class))))
                    .as("annotated with any [%s]", String.join(", ", annotationTypes));
        }
    }
}
