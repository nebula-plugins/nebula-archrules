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
    api(kotlin("metadata-jvm")) // api needed for transitive dependencies in a rule classpath

    testImplementation(kotlin("reflect"))
    testImplementation(libs.assertj)
    testImplementation(libs.logback)
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
        // this suite is for testing that kotlin-related rules work without kotlin on the classpath
        create<JvmTestSuite>("javaOnlyTest") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(libs.assertj)
            }
            project.configurations.named("javaOnlyTestRuntimeClasspath") {
                exclude(module = "kotlin-reflect")
                exclude(module = "kotlin-stdlib")
            }
            targets.configureEach {
                project.tasks.named("check") {
                    dependsOn(testTask)
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}
