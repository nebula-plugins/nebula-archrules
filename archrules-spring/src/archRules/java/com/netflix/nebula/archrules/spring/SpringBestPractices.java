package com.netflix.nebula.archrules.spring;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.Map;

@NullMarked
public class SpringBestPractices implements ArchRulesService {
    @Override
    public Map<String, ArchRule> getRules() {
        return Collections.singletonMap("no field injection", NoFieldInjectionRule.RULE);
    }
}
