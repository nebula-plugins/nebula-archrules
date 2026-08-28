import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.netflix.nebula.library")
    kotlin("jvm")
}
description = "Common Predicates and Chainable Functions for building rules"

dependencies {
    implementation(libs.jspecify)
    api("com.tngtech.archunit:archunit:1.+")
    compileOnly(kotlin("reflect"))

    testImplementation(libs.assertj)
    testImplementation(libs.logback)
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation(kotlin("reflect"))
    testImplementation("com.netflix.nebula:nebula-archrules-core:1.+")
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
tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinJavaToolchain.toolchain.use(javaToolchains.launcherFor {
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

kotlin {
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}
