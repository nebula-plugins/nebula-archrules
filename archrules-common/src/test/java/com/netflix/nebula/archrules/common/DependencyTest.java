package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.other.ClassInOtherPackage;
import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.common.Util.scanClass;
import static org.assertj.core.api.Assertions.assertThat;

public class DependencyTest {

    @Test
    public void test_same() {
        assertThat(Dependency.Predicates.resideInSamePackage()
                .test(scanClass(SameUsage.class).getDirectDependenciesFromSelf().stream()
                        .filter(d -> d.getTargetClass().isAssignableTo(OtherUsage.class))
                        .findFirst().get()))
                .isTrue();
    }

    @Test
    public void test_other() {
        assertThat(Dependency.Predicates.resideInSamePackage()
                .test(scanClass(OtherUsage.class).getDirectDependenciesFromSelf().stream()
                        .filter(d -> d.getTargetClass().isAssignableTo(ClassInOtherPackage.class))
                        .findFirst().get()))
                .isFalse();
    }

    static class OtherUsage {
        ClassInOtherPackage otherPackage;
    }

    static class SameUsage {
        OtherUsage samePackage;
    }
}
