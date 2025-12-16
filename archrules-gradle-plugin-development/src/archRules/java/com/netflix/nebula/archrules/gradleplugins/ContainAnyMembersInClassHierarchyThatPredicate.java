package com.netflix.nebula.archrules.gradleplugins;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMember;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.function.Function;

@NullMarked
public class ContainAnyMembersInClassHierarchyThatPredicate <T extends JavaMember> extends DescribedPredicate<JavaClass> {
    private final Function<JavaClass, Set<T>> getMembers;
    private final DescribedPredicate<? super T> predicate;

    ContainAnyMembersInClassHierarchyThatPredicate(String memberDescription, Function<JavaClass, Set<T>> getMembers, DescribedPredicate<? super T> predicate) {
        super("contain any " + memberDescription + " that " + predicate.getDescription());
        this.getMembers = getMembers;
        this.predicate = predicate;
    }

    @Override
    public boolean test(JavaClass input) {
        return getMembers.apply(input).stream().anyMatch(predicate);
    }
}
