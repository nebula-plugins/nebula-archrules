plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for annotating null-safe public APIs"
repositories {
    mavenCentral()
}
dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("org.jspecify:jspecify:1.0.0")
    archRulesTestImplementation("com.google.code.findbugs:jsr305:3.0.2")
    archRulesTestImplementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    archRulesTestImplementation("org.jetbrains:annotations:26.0.2-1")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11) // must be 11 in order to reference jakarta annotations
    }
}
dependencyLocking {
    lockAllConfigurations()
}
