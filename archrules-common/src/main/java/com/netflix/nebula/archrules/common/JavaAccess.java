package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JavaAccess {
    public static class Predicates {

        /**
         * tests that an access's origin and target are in the same package
         */
        public static DescribedPredicate<com.tngtech.archunit.core.domain.JavaAccess<?>> targetHasOwnerInSamePackage() {
            return new DescribedPredicate<com.tngtech.archunit.core.domain.JavaAccess<?>>(
                    "in the same package") {
                @Override
                public boolean test(com.tngtech.archunit.core.domain.JavaAccess javaAccess) {
                    return javaAccess.getOriginOwner().getPackage()
                            .equals(javaAccess.getTargetOwner().getPackage());
                }
            };
        }
    }
}
