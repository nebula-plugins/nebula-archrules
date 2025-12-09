import com.netflix.nebula.archrules.gradle.CheckRulesTask
import com.netflix.nebula.archrules.gradle.PrintConsoleReportTask

plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of Junit4 and Testcontainers 1.x"
repositories {
    mavenCentral()
}
dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("junit:junit:4.12")
    archRulesTestImplementation("org.testcontainers:testcontainers:1.21.3")
    archRulesTestImplementation("org.testcontainers:postgresql:1.21.3")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
dependencyLocking {
    lockAllConfigurations()
}
