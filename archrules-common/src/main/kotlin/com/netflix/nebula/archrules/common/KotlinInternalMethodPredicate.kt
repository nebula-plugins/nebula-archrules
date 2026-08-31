package com.netflix.nebula.archrules.common

import com.tngtech.archunit.base.DescribedPredicate
import kotlin.collections.mapIndexed
import kotlin.reflect.KVisibility
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

class KotlinInternalMethodPredicate : DescribedPredicate<com.tngtech.archunit.core.domain.JavaMethod>(
    "Kotlin internal method"
) {

    /**
     * @param javaMethod declared as nullable in order to avoid static references to the kotlin Intrinsics class
     */
    override fun test(javaMethod: com.tngtech.archunit.core.domain.JavaMethod?): Boolean {
        return javaMethod != null && if (javaMethod.owner.isAnnotatedWith("kotlin.Metadata")) {
            val functions = javaMethod.owner.reflect().kotlin.functions
            val matchingFunction = functions
                .firstOrNull {
                    it.javaMethod?.name == javaMethod.name && it.valueParameters.mapIndexed { index, parameter ->
                        parameter.type.jvmErasure.qualifiedName == javaMethod.parameters[index].type.toErasure().fullName
                    }.all { true }
                }
            matchingFunction != null && matchingFunction.visibility == KVisibility.INTERNAL
        } else {
            false
        }
    }
}
