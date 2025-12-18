package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GradlePluginExtensionProviderApiRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradlePluginExtensionProviderApiRuleTest.class);

    @Test
    public void extensionWithPlainStringField_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_FIELDS_USE_PROVIDER_API,
                BadPluginExtension.class,
                PluginUsingBadExtension.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("BadPluginExtension.value");
        assertThat(result.getFailureReport().toString()).contains("has type String");
        assertThat(result.getFailureReport().toString()).contains("Use Property<String>");
    }

    @Test
    public void extensionWithPropertyField_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_FIELDS_USE_PROVIDER_API,
                GoodPluginExtension.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void extensionWithPlainStringField_another_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_FIELDS_USE_PROVIDER_API,
                AnotherBadPluginExtension.class,
                PluginUsingAnotherBadExtension.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("AnotherBadPluginExtension.value");
        assertThat(result.getFailureReport().toString()).contains("has type String");
        assertThat(result.getFailureReport().toString()).contains("Use Property<String>");
    }

    @Test
    public void extensionWithAbstractGetter_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_FIELDS_USE_PROVIDER_API,
                AnotherGoodPluginExtension.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void extensionWithStaticField_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_FIELDS_USE_PROVIDER_API,
                PluginExtensionWithStatics.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void extensionWithConcreteProviderGetter_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_ABSTRACT_GETTERS,
                ConcretePluginExtension.class,
                PluginUsingConcreteExtension.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("should be abstract");
    }

    @Test
    public void extensionWithAbstractProviderGetter_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginExtensionProviderApiRule.EXTENSION_ABSTRACT_GETTERS,
                AbstractPluginExtension.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class BadPluginExtension {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @SuppressWarnings("unused")
    public abstract static class GoodPluginExtension {
        public abstract Property<String> getValue();
    }

    @SuppressWarnings("unused")
    public static class AnotherBadPluginExtension {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @SuppressWarnings("unused")
    public abstract static class AnotherGoodPluginExtension {
        public abstract Property<String> getValue();
    }

    @SuppressWarnings("unused")
    public static class PluginExtensionWithStatics {
        private static final String CONSTANT = "constant";

        public static String getConstant() {
            return CONSTANT;
        }
    }

    @SuppressWarnings("unused")
    public static class ConcretePluginExtension {
        private final Property<String> value;

        public ConcretePluginExtension(Property<String> value) {
            this.value = value;
        }

        public Property<String> getValue() {
            return value;
        }
    }

    @SuppressWarnings("unused")
    public abstract static class AbstractPluginExtension {
        public abstract Property<String> getValue();
    }

    @SuppressWarnings("unused")
    public static class PluginUsingBadExtension implements Plugin<Project> {
        private BadPluginExtension ext = new BadPluginExtension();

        @Override
        public void apply(Project project) {
            ext.getValue();
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingAnotherBadExtension implements Plugin<Project> {
        private AnotherBadPluginExtension ext = new AnotherBadPluginExtension();

        @Override
        public void apply(Project project) {
            ext.getValue();
        }
    }

    @SuppressWarnings("unused")
    public static class PluginUsingConcreteExtension implements Plugin<Project> {
        private ConcretePluginExtension ext;

        public PluginUsingConcreteExtension(Property<String> prop) {
            ext = new ConcretePluginExtension(prop);
        }

        @Override
        public void apply(Project project) {
            ext.getValue();
        }
    }
}
