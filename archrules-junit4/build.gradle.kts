plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of Junit4"
repositories {
    mavenCentral()
}
dependencies {
    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)

    archRulesTestImplementation("junit:junit:4.12")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}
dependencyLocking {
    lockAllConfigurations()
}
