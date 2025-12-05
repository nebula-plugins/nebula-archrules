plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
description = "Arch Rules for detecting usage of identified insecure APIs"
repositories {
    mavenCentral()
}
dependencies {
    archRulesImplementation(libs.jspecify)

    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
    archRulesTestImplementation("com.google.guava:guava:23.0")
    archRulesTestImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    archRulesTestImplementation("org.eclipse.jetty:jetty-servlet:9.+")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
dependencyLocking {
    lockAllConfigurations()
}
