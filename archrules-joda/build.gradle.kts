plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of Joda"
repositories {
    mavenCentral()
}
dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("joda-time:joda-time:2.14.0")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
dependencyLocking {
    lockAllConfigurations()
}
