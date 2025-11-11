plugins {
    id("com.netflix.nebula.library")
    id("com.netflix.nebula.archrules.library")
}
repositories {
    mavenCentral()
}
dependencies {
    archRulesTestImplementation(libs.assertj)
    archRulesTestImplementation(libs.logback)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}
dependencyLocking {
    lockAllConfigurations()
}
