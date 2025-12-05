package com.netflix.nebula.archrules.security;

import com.google.common.io.FileBackedOutputStream;
import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import kotlin.io.FilesKt;
import org.eclipse.jetty.http.HttpURI;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class CveArchRulesTest {
    @Test
    public void test_pass() {
        EvaluationResult result = Runner.check(CveArchRules.CVE_2023_2976, PassingClass.class);
        assertThat(result.hasViolation())
                .isFalse();
    }

    @Test
    public void test_fail() {
        EvaluationResult result = Runner.check(CveArchRules.CVE_2023_2976, FailingClass.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(1);
    }

    @Test
    public void test_fail_shaded() {
        EvaluationResult result = Runner.check(CveArchRules.CVE_2023_2976, FailingClassShaded.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(1);
    }

    @Test
    public void test_kotlin_cve_fail_file() {
        EvaluationResult result = Runner.check(CveArchRules.CVE_2020_29582, KotlinFailingClassFile.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(1);
    }

    @Test
    public void test_kotlin_cve_fail_dir() {
        EvaluationResult result = Runner.check(CveArchRules.CVE_2020_29582, KotlinFailingClassDir.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(1);
    }

    @Test
    public void test_jetty() {
        EvaluationResult result = Runner.check(CveArchRules.CVE_2024_6763, UsesJettyHttpURI.class);
        assertThat(result.hasViolation())
                .isTrue();
        assertThat(result.getFailureReport().getDetails())
                .hasSize(1);
    }

    static class KotlinFailingClassFile {
        File thing = FilesKt.createTempFile("tmp", null, null);
    }

    static class KotlinFailingClassDir {
        File thing = FilesKt.createTempDir("tmp", null, null);
    }

    static class FailingClass {
        FileBackedOutputStream fileBackedOutputStream;
    }

    static class FailingClassShaded {
        com.tngtech.archunit.thirdparty.com.google.common.io.FileBackedOutputStream fileBackedOutputStream;
    }

    static class PassingClass {
    }

    static class UsesJettyHttpURI {
        HttpURI httpURI;
    }
}
