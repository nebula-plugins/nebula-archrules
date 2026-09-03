package com.netflix.nebula.archrules.common

import com.netflix.nebula.archrules.common.examples.PublicKotlinClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaMethod

internal class KotlinInternalMethodPredicateTest {

    @Test
    fun test_kotlinInternal_public() {
        assertThat(
            KotlinInternalMethodPredicate()
                .test(Util.scanClass(PublicKotlinClass::class.java).getMethod("publicMethod"))
        ).isFalse()
    }

    @Test
    fun test_kotlinInternal_internal() {
        val internalMethodName = PublicKotlinClass::class.functions.first().javaMethod?.name
        val scannedClass = Util.scanClass(PublicKotlinClass::class.java)
        assertThat(KotlinInternalMethodPredicate().test(scannedClass.getMethod(internalMethodName)))
            .isTrue()
    }

    @Test
    fun test_kotlinInternal_internal_other_signatures() {
        val internalMethods = PublicKotlinClass::class.functions.filter { it.name == "manyParams" }
        assertThat(internalMethods).hasSize(1)
        val scannedClass = Util.scanClass(PublicKotlinClass::class.java)
        val javaName = internalMethods.first().javaMethod!!.name
        assertThat(
            KotlinInternalMethodPredicate()
                .test(scannedClass.getMethod(javaName, String::class.java))
        )
            .`as`("jvm overload is detected")
            .isTrue()
        assertThat(
            KotlinInternalMethodPredicate()
                .test(scannedClass.getMethod(javaName, String::class.java, String::class.java))
        )
            .`as`("full signature is detected")
            .isTrue()
    }

    @Test
    fun test_kotlinInternal_description() {
        assertThat(KotlinInternalMethodPredicate().description).isEqualTo("Kotlin internal method")
    }
}
