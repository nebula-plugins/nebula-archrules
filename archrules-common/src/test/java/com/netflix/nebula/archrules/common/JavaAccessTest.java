package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.other.ClassInOtherPackage;
import org.junit.jupiter.api.Test;

import static com.netflix.nebula.archrules.common.Util.scanClass;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaAccessTest {
    @Test
    public void test_same() {
        assertThat(JavaAccess.Predicates.targetHasOwnerInSamePackage()
                .test(scanClass(JavaAccessTest.SameUsage.class).getAccessesFromSelf().stream()
                        .filter(d -> d.getTargetOwner().isAssignableTo(JavaAccessTest.OtherUsage.class))
                        .findFirst().get()))
                .isTrue();
    }

    @Test
    public void test_other() {
        assertThat(JavaAccess.Predicates.targetHasOwnerInSamePackage()
                .test(scanClass(JavaAccessTest.OtherUsage.class).getAccessesFromSelf().stream()
                        .filter(d -> d.getTargetOwner().isAssignableTo(ClassInOtherPackage.class))
                        .findFirst().get()))
                .isFalse();
    }

    static class OtherUsage {
        static void usage() {
            ClassInOtherPackage.aMethod();
        }
    }

    static class SameUsage {
        static void usage() {
            OtherUsage.usage();
        }
    }
}
