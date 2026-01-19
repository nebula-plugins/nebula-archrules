package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import org.jspecify.annotations.NullMarked;

/**
 * Matches getter methods (getX(), isX()) that follow JavaBeans naming conventions.
 */
@NullMarked
class JavaBeanGetterPredicate extends DescribedPredicate<JavaMethod> {

    JavaBeanGetterPredicate() {
        super("getter");
    }

    @Override
    public boolean test(JavaMethod input) {
        if (input.getModifiers().contains(JavaModifier.STATIC)) {
            return false;
        }
        if (!input.getParameters().isEmpty()) {
            return false;
        }
        if (input.getName().startsWith("get")) {
            return input.getName().length() >= 4 && !Character.isLowerCase(input.getName().charAt(3));
        }
        if (input.getName().startsWith("is")) {
            if (input.getRawReturnType().isAssignableTo(Boolean.class)) {
                return false;
            }
            return !Character.isLowerCase(input.getName().charAt(2));
        }
        return false;
    }
}
