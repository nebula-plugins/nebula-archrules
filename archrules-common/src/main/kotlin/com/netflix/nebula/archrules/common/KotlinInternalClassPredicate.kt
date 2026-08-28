package com.netflix.nebula.archrules.common

import com.tngtech.archunit.base.DescribedPredicate
import kotlin.reflect.KVisibility

class KotlinInternalClassPredicate : DescribedPredicate<com.tngtech.archunit.core.domain.JavaClass>(
    "Kotlin internal class"
) {
    override fun test(javaClass: com.tngtech.archunit.core.domain.JavaClass): Boolean {
        return javaClass.isKotlinInternal()
    }

    private fun com.tngtech.archunit.core.domain.JavaClass.isKotlinInternal(): Boolean {
        return this.isAnnotatedWith("kotlin.Metadata") &&
            this.reflect().kotlin.visibility == KVisibility.INTERNAL
    }
}
