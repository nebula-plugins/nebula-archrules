package com.netflix.nebula.archrules.common.examples

class PublicKotlinClass {
    internal fun internalKotlinMethod() {

    }

    fun publicMethod() {

    }

    @JvmOverloads
    internal fun manyParams(param1: String, param2: String = "test") {}

    @JvmOverloads
    fun publicManyParams(param1: String, param2: String = "test") {}
}
