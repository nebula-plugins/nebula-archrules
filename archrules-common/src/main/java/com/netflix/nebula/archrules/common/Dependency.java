package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Dependency {
    public static class Predicates {

        /**
         * tests that a class and a dependency are in the same package
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.Dependency> resideInSamePackage() {
            return new DescribedPredicate<com.tngtech.archunit.core.domain.Dependency>("reside in same package") {
                @Override
                public boolean test(com.tngtech.archunit.core.domain.Dependency dependency) {
                    return dependency.getOriginClass().getPackageName()
                            .equals(dependency.getTargetClass().getPackageName());
                }
            };
        }
    }
}
