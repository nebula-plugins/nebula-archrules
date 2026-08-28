package com.netflix.nebula.archrules.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaMethod

internal class KotlinInternalMethodPredicateTest {

    @Test
    fun test_kotlinInternal_public() {
        assertThat(
            JavaMethod.Predicates.kotlinInternal()
                .test(Util.scanClass(PublicKotlinClass::class.java).getMethod("publicMethod"))
        ).isFalse()
    }

    @Test
    fun test_kotlinInternal_internal() {
        val internalMethodName = PublicKotlinClass::class.functions.first().javaMethod?.name
        val scannedClass = Util.scanClass(PublicKotlinClass::class.java)
        assertThat(JavaMethod.Predicates.kotlinInternal().test(scannedClass.getMethod(internalMethodName))
        ).isTrue()
    }

    @Test
    fun test_kotlinInternal_description() {
        assertThat( JavaMethod.Predicates.kotlinInternal().description).isEqualTo("Kotlin internal method")
    }

    class PublicKotlinClass {
        internal fun internalKotlinMethod() {

        }
        fun publicMethod(){

        }
    }
}
