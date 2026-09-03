package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import kotlin.Metadata;
import kotlin.metadata.Attributes;
import kotlin.metadata.Visibility;
import kotlin.metadata.jvm.KotlinClassMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class KotlinInternalClassPredicate extends DescribedPredicate<JavaClass> {
    public KotlinInternalClassPredicate() {
        super("Kotlin internal class");
    }

    @Override
    public boolean test(@Nullable JavaClass javaClass) {
        if (javaClass != null && javaClass.isAnnotatedWith("kotlin.Metadata")) {
            KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(javaClass.getAnnotationOfType(Metadata.class));
            if (metadata instanceof KotlinClassMetadata.Class) {
                return Attributes.getVisibility(((KotlinClassMetadata.Class) metadata).getKmClass()) == Visibility.INTERNAL;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
