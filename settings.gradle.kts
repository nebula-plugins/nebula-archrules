pluginManagement {
    plugins {
        id("com.netflix.nebula.root") version ("25.1.2")
        id("com.netflix.nebula.library") version ("25.1.2")
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
include(":archrules-nullability")