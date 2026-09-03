package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import kotlin.Metadata;
import kotlin.metadata.Attributes;
import kotlin.metadata.KmFunction;
import kotlin.metadata.Visibility;
import kotlin.metadata.jvm.KotlinClassMetadata;

import java.util.List;
import java.util.Optional;

import static com.netflix.nebula.archrules.common.KotlinMetadataUtil.matchFunction;

class KotlinInternalMethodPredicate extends DescribedPredicate<JavaMethod> {

    KotlinInternalMethodPredicate() {
        super("Kotlin internal method");
    }

    @Override
    public boolean test(JavaMethod javaMethod) {
        if (javaMethod != null && javaMethod.getOwner().isAnnotatedWith("kotlin.Metadata")) {
            KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(javaMethod.getOwner().getAnnotationOfType(Metadata.class));
            if (metadata instanceof KotlinClassMetadata.Class) {
                List<KmFunction> functions = ((KotlinClassMetadata.Class) metadata).getKmClass().getFunctions();
                Optional<KmFunction> matchingFunction = matchFunction(javaMethod, functions);
                return matchingFunction
                        .filter(it -> Attributes.getVisibility(it) == Visibility.INTERNAL)
                        .isPresent();
            } else if (metadata instanceof KotlinClassMetadata.FileFacade) {
                List<KmFunction> functions = ((KotlinClassMetadata.FileFacade) metadata).getKmPackage().getFunctions();
                Optional<KmFunction> matchingFunction = matchFunction(javaMethod, functions);
                return matchingFunction
                        .filter(it -> Attributes.getVisibility(it) == Visibility.INTERNAL)
                        .isPresent();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
