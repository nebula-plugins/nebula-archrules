plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of deprecated code"
repositories {
    mavenCentral()
}
dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
tasks.named<JavaCompile>("compileArchRulesTestJava") {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(11))
    })
}
dependencyLocking {
    lockAllConfigurations()
}
