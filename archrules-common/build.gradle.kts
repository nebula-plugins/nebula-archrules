plugins {
    id("com.netflix.nebula.library")
}
description = "Common Predicates and Chanable Functions for building rules"

dependencies {
    implementation(libs.jspecify)
    api("com.tngtech.archunit:archunit:1.+")

    testImplementation(libs.assertj)
    testImplementation(libs.logback)
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
    testImplementation("com.netflix.nebula:nebula-archrules-core:0.+")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
tasks.named<JavaCompile>("compileTestJava") {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(11))
    })
}
dependencyLocking {
    lockAllConfigurations()
}
testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}
