plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of Guava"

dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("com.google.guava:guava:33.5.0-jre")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencyLocking {
    lockAllConfigurations()
}
