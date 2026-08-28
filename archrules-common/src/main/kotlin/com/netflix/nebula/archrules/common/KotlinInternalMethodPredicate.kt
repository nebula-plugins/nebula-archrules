package com.netflix.nebula.archrules.common

import com.tngtech.archunit.base.DescribedPredicate
import kotlin.reflect.KVisibility
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

class KotlinInternalMethodPredicate : DescribedPredicate<com.tngtech.archunit.core.domain.JavaMethod>(
    "Kotlin internal method"
) {
    override fun test(javaMethod: com.tngtech.archunit.core.domain.JavaMethod): Boolean {
        return javaMethod.isKotlinInternal()
    }

    private fun com.tngtech.archunit.core.domain.JavaMethod.isKotlinInternal(): Boolean {
        return if (owner.isAnnotatedWith("kotlin.Metadata")) {
            val functions = owner.reflect().kotlin.functions
            val matchingFunction = functions
                .firstOrNull {
                    it.javaMethod?.name == this.name && it.valueParameters.mapIndexed { index, parameter ->
                        parameter.type.jvmErasure.qualifiedName == parameters[index].type.toErasure().fullName
                    }.all { true }
                }
            matchingFunction != null && matchingFunction.visibility == KVisibility.INTERNAL
        } else {
            false
        }
    }
}
