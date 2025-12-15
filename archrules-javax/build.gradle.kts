plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of javax"

dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("javax:javaee-api:8.0.1")
    archRulesTestImplementation("javax.servlet:javax.servlet-api:4.0.1")
    archRulesTestImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
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
