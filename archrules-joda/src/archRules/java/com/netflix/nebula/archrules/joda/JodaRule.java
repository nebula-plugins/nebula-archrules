package com.netflix.nebula.archrules.joda;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.Map;

@NullMarked
public class JodaRule implements ArchRulesService {
    /**
     * This rule is a stop-gap to find all usages of Joda.
     */
    public static ArchRule jodaRule = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses()
            .should(GeneralCodingRules.USE_JODATIME)
            .allowEmptyShould(true)
            .as("No code should use Joda time library")
            .because("usage of Joda is deprecated. Please migrate to java.time.");

    @Override
    public Map<String, ArchRule> getRules() {
        return Collections.singletonMap("jodaRule", jodaRule);
    }
}
