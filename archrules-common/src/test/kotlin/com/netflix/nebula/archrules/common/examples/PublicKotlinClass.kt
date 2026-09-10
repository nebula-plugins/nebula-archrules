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

    @JvmOverloads
    fun publicManyParamsDifferentTypes(param1: Int, param2: String = "test", param3: String, param4: Int = 2) {}

    fun publicManyParamsDifferentTypes(param1: List<String>) {}

    @JvmOverloads
    fun defaultParams(a: ClassA, param2: String = "test", param3: Int) {}
    @JvmOverloads
    fun defaultParams(b: ClassB, param2: String = "test", param3: String) {}

    fun <T> generic(param1: T, param2: String = "test") {}
}
