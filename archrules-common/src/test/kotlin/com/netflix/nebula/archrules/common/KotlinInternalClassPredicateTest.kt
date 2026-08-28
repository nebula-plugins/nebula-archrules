package com.netflix.nebula.archrules.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class KotlinInternalClassPredicateTest {
    @Test
    fun test_kotlinInternal_public() {
        assertThat(
            JavaClass.Predicates.kotlinInternal().test(Util.scanClass(PublicKotlinClass::class.java))
        ).isFalse()
    }

    @Test
    fun test_kotlinInternal_internal() {
        assertThat(
            JavaClass.Predicates.kotlinInternal().test(Util.scanClass(InternalKotlinClass::class.java))
        ).isTrue()
    }

    @Test
    fun test_kotlinInternal_description() {
        assertThat(JavaClass.Predicates.kotlinInternal().description).isEqualTo("Kotlin internal class")
    }

    internal class InternalKotlinClass
    class PublicKotlinClass {
        internal fun internalKotlinMethod() {

        }
    }
}
