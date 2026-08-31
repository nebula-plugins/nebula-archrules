package com.netflix.nebula.archrules.common

import com.netflix.nebula.archrules.common.examples.InternalKotlinClass
import com.netflix.nebula.archrules.common.examples.PublicKotlinClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.jvm.java

internal class KotlinInternalClassPredicateTest {
    @Test
    fun test_kotlinInternal_public() {
        assertThat(
            KotlinInternalClassPredicate().test(Util.scanClass(PublicKotlinClass::class.java))
        ).isFalse()
    }

    @Test
    fun test_kotlinInternal_internal() {
        assertThat(
            KotlinInternalClassPredicate().test(Util.scanClass(InternalKotlinClass::class.java))
        ).isTrue()
    }

    @Test
    fun test_kotlinInternal_description() {
        assertThat(JavaClass.Predicates.kotlinInternal().description).isEqualTo("Kotlin internal class")
    }

}
