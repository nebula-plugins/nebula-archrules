pluginManagement {
    plugins {
        id("com.netflix.nebula.root") version ("25.0.1")
        id("com.netflix.nebula.library") version ("25.0.1")
        // we should keep the major version of this project in sync with the archrules.library version it is based on
        // we will align with the minor version until 1.0
        id("com.netflix.nebula.archrules.library") version ("0.1.+")
    }
}
plugins {
    id("com.gradle.develocity") version("4.2")
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

rootProject.name = "nebula-archrules"

include(":archrules-deprecation")
include(":archrules-testing-frameworks")
include(":archrules-joda")
