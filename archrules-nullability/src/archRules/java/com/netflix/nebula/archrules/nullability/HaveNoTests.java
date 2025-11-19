package com.netflix.nebula.archrules.nullability;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;

public class HaveNoTests extends DescribedPredicate<JavaClass> {
    public HaveNoTests() {
        super("have no tests");
    }

    @Override
    public boolean test(JavaClass javaClass) {
        return javaClass.getMembers().stream()
                .noneMatch(it -> it.isAnnotatedWith("org.junit.jupiter.api.Test"));
    }
}
