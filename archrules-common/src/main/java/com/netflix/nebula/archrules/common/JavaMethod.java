package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JavaMethod {
    public static class Predicates {

        /**
         * checks if a method is a getter according to JavaBean conventions
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.JavaMethod> aGetter() {
            return new JavaBeanGetterPredicate();
        }

        /**
         * checks if the class's package is annotated with a specific annotation
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.JavaMethod> kotlinInternal() {
            return new KotlinInternalMethodPredicate();
        }
    }
}
