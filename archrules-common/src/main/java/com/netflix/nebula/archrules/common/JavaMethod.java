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
    }
}
