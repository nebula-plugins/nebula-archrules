plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of deprecated code"
repositories {
    mavenCentral()
}
dependencies {
    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
dependencyLocking {
    lockAllConfigurations()
}
