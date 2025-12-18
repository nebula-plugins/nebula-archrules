package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class GradlePluginServiceInjectionRuleTest {
    private static final Logger LOG = LoggerFactory.getLogger(GradlePluginServiceInjectionRuleTest.class);

    @Test
    public void pluginCallingGetObjects_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginServiceInjectionRule.USE_INJECTED_OBJECT_FACTORY,
                PluginCallingGetObjects.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("calls getObjects");
        assertThat(result.getFailureReport().toString()).contains("inject ObjectFactory");
    }

    @Test
    public void pluginWithInjectedObjectFactory_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginServiceInjectionRule.USE_INJECTED_OBJECT_FACTORY,
                PluginWithInjectedObjectFactory.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @Test
    public void pluginCallingGetProviders_should_fail() {
        final EvaluationResult result = Runner.check(
                GradlePluginServiceInjectionRule.USE_INJECTED_PROVIDER_FACTORY,
                PluginCallingGetProviders.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isTrue();
        assertThat(result.getFailureReport().toString()).contains("calls getProviders");
        assertThat(result.getFailureReport().toString()).contains("inject ProviderFactory");
    }

    @Test
    public void pluginWithInjectedProviderFactory_should_pass() {
        final EvaluationResult result = Runner.check(
                GradlePluginServiceInjectionRule.USE_INJECTED_PROVIDER_FACTORY,
                PluginWithInjectedProviderFactory.class
        );
        LOG.info(result.getFailureReport().toString());
        assertThat(result.hasViolation()).isFalse();
    }

    @SuppressWarnings("unused")
    public static class PluginCallingGetObjects implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            ObjectFactory objects = project.getObjects();
            project.getTasks().register("myTask");
        }
    }

    @SuppressWarnings("unused")
    public static class PluginWithInjectedObjectFactory implements Plugin<Project> {
        private final ObjectFactory objects;

        @Inject
        public PluginWithInjectedObjectFactory(ObjectFactory objects) {
            this.objects = objects;
        }

        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask");
        }
    }

    @SuppressWarnings("unused")
    public static class PluginCallingGetProviders implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            ProviderFactory providers = project.getProviders();
            project.getTasks().register("myTask");
        }
    }

    @SuppressWarnings("unused")
    public static class PluginWithInjectedProviderFactory implements Plugin<Project> {
        private final ProviderFactory providers;

        @Inject
        public PluginWithInjectedProviderFactory(ProviderFactory providers) {
            this.providers = providers;
        }

        @Override
        public void apply(Project project) {
            project.getTasks().register("myTask");
        }
    }
}
