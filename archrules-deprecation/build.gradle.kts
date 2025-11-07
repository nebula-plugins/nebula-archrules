plugins {
    java
    id("com.netflix.nebula.archrules.library")
}
repositories {
    mavenCentral()
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}
dependencyLocking {
    lockAllConfigurations()
}