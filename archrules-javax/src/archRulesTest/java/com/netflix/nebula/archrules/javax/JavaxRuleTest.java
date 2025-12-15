package com.netflix.nebula.archrules.javax;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaxRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(JavaxRuleTest.class);

    @Test
    public void test_javax_servlet_usage() {
        final EvaluationResult result = Runner.check(JavaxRule.javaxRule, JavaxServlet.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().getDetails()).hasSize(2);
    }

    @Test
    public void test_javax_rest_usage() {
        final EvaluationResult result = Runner.check(JavaxRule.javaxRule, JavaxRestService.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().getDetails()).hasSize(2);
    }

    @Test
    public void test_jakarta_usage() {
        final EvaluationResult result = Runner.check(JavaxRule.javaxRule, JakartaUsage.class);
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class JavaxServlet extends HttpServlet {
    }

    @SuppressWarnings("unused")
    @Path("/hello")
    public static class JavaxRestService {
        @GET
        public String hello() {
            return "hello";
        }
    }

    @SuppressWarnings("unused")
    public static class JakartaUsage extends jakarta.servlet.http.HttpServlet {
    }

}
