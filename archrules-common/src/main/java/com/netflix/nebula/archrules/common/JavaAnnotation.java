package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JavaAnnotation {
    public static class Predicates {

        /**
         * Java deprecation annotation with the forRemoval property set to true
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.JavaAnnotation<?>> deprecatedForRemoval() {
            return new DescribedPredicate<com.tngtech.archunit.core.domain.JavaAnnotation<?>>("@Deprecated(forRemoval=true)") {
                @Override
                public boolean test(com.tngtech.archunit.core.domain.JavaAnnotation<?> annotation) {
                    if (!annotation.getRawType().isAssignableTo(Deprecated.class)) {
                        return false;
                    }
                    return annotation.get("forRemoval")
                            .filter(it -> it instanceof Boolean)
                            .map(value -> (Boolean) value)
                            .orElse(false);
                }
            };
        }
    }
}
