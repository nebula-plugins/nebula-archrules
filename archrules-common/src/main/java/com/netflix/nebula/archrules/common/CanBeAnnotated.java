package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import org.jspecify.annotations.NullMarked;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

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
    }
}
