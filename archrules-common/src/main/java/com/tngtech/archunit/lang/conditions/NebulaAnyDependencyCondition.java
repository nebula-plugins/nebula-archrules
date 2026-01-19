package com.tngtech.archunit.lang.conditions;


import com.tngtech.archunit.PublicAPI;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

import static com.tngtech.archunit.PublicAPI.Usage.ACCESS;
import static com.tngtech.archunit.base.DescribedPredicate.alwaysFalse;

/**
 * Duplicate of {@link AnyDependencyCondition}.
 * @deprecated Can be removed once <a href="https://github.com/TNG/ArchUnit/pull/1580">haveDependenciesThat</a> is merged.
 */
@Deprecated
@NullMarked
public final class NebulaAnyDependencyCondition extends AnyAttributeMatchesCondition<Dependency> {
    private final DescribedPredicate<? super Dependency> conditionPredicate;
    private final Function<JavaClass, ? extends Collection<Dependency>> javaClassToRelevantDependencies;
    private final DescribedPredicate<Dependency> ignorePredicate;

    public NebulaAnyDependencyCondition(
            String description,
            DescribedPredicate<? super Dependency> predicate,
            Function<JavaClass, ? extends Collection<Dependency>> javaClassToRelevantDependencies) {

        this(description, predicate, javaClassToRelevantDependencies, alwaysFalse());
    }

    private NebulaAnyDependencyCondition(
            String description,
            DescribedPredicate<? super Dependency> conditionPredicate,
            Function<JavaClass, ? extends Collection<Dependency>> javaClassToRelevantDependencies,
            DescribedPredicate<Dependency> ignorePredicate) {

        super(description, new DependencyCondition(conditionPredicate));
        this.conditionPredicate = conditionPredicate;
        this.javaClassToRelevantDependencies = javaClassToRelevantDependencies;
        this.ignorePredicate = ignorePredicate;
    }

    @PublicAPI(usage = ACCESS)
    public NebulaAnyDependencyCondition ignoreDependency(DescribedPredicate<? super Dependency> ignorePredicate) {
        return new NebulaAnyDependencyCondition(getDescription(),
                conditionPredicate,
                javaClassToRelevantDependencies,
                this.ignorePredicate.or(ignorePredicate));
    }

    @Override
    @PublicAPI(usage = ACCESS)
    public NebulaAnyDependencyCondition as(String description, Object... args) {
        return new NebulaAnyDependencyCondition(
                String.format(description, args),
                conditionPredicate,
                javaClassToRelevantDependencies,
                ignorePredicate);
    }

    @Override
    Collection<Dependency> relevantAttributes(JavaClass javaClass) {
        Collection<Dependency> result = new HashSet<>();
        for (Dependency dependency : javaClassToRelevantDependencies.apply(javaClass)) {
            if (!ignorePredicate.test(dependency)) {
                result.add(dependency);
            }
        }
        return result;
    }
}
