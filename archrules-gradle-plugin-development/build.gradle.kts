plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}

description = "Arch Rules for detecting bad practices when developing Gradle plugins"

dependencies {
    archRulesImplementation(project(":archrules-common"))
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation(gradleApi())
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencyLocking {
    lockAllConfigurations()
}
