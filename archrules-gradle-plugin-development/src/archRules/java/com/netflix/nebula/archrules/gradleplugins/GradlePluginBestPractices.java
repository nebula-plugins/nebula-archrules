package com.netflix.nebula.archrules.gradleplugins;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static com.netflix.nebula.archrules.gradleplugins.GradleDeprecatedApiRule.pluginsShouldNotUseDeprecatedGradleApis;
import static com.netflix.nebula.archrules.gradleplugins.GradleDeprecatedApiRule.tasksShouldNotUseDeprecatedGradleApis;
import static com.netflix.nebula.archrules.gradleplugins.GradleInternalApiRule.PLUGIN_INTERNAL;
import static com.netflix.nebula.archrules.gradleplugins.GradleInternalApiRule.TASK_INTERNAL;
import static com.netflix.nebula.archrules.gradleplugins.GradlePluginLazyTaskRegistrationRule.LAZY_TASK_CREATION;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskActionRule.taskActionShouldNotAccessProject;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskActionRule.taskActionShouldNotCallGetTaskDependencies;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskCacheabilityRule.FIELDS_PATH_SENSITIVITY;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskCacheabilityRule.METHODS_PATH_SENSITIVITY;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskInputOutputRule.INPUTS_OUTPUTS;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskProviderApiRule.ABSTRACT_GETTERS;
import static com.netflix.nebula.archrules.gradleplugins.GradleTaskProviderApiRule.PROVIDER_PROPERTIES;

@NullMarked
@SuppressWarnings("unused")
public class GradlePluginBestPractices implements ArchRulesService {
    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("provider properties", PROVIDER_PROPERTIES);
        rules.put("abstract getters", ABSTRACT_GETTERS);
        rules.put("task project access", taskActionShouldNotAccessProject);
        rules.put("task dependencies", taskActionShouldNotCallGetTaskDependencies);
        rules.put("lazy task registration", LAZY_TASK_CREATION);
        rules.put("Plugin using deprecated gradle APIs", pluginsShouldNotUseDeprecatedGradleApis);
        rules.put("Task using deprecated gradle APIs", tasksShouldNotUseDeprecatedGradleApis);
        rules.put("Plugin using internal gradle APIs", PLUGIN_INTERNAL);
        rules.put("Task using internal gradle APIs", TASK_INTERNAL);
        rules.put("Task declares inputs and/or outputs", INPUTS_OUTPUTS);
        rules.put("Cacheable Task input field path sensitivity", FIELDS_PATH_SENSITIVITY);
        rules.put("Cacheable Task input method path sensitivity", METHODS_PATH_SENSITIVITY);
        return rules;
    }
}
