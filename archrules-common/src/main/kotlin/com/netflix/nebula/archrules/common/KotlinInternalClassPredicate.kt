package com.netflix.nebula.archrules.common

import com.tngtech.archunit.base.DescribedPredicate

class KotlinInternalClassPredicate : DescribedPredicate<com.tngtech.archunit.core.domain.JavaClass>(
    "Kotlin internal class"
) {
    /**
     * @param javaClass declared as nullable in order to avoid static references to the kotlin Intrinsics class
     */
    override fun test(javaClass: com.tngtech.archunit.core.domain.JavaClass?): Boolean {
        return javaClass != null && javaClass.isAnnotatedWith("kotlin.Metadata") &&
            javaClass.reflect().kotlin.visibility.toString() == "INTERNAL"
    }
}
