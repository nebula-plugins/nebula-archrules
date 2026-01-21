pluginManagement {
    plugins {
        id("com.netflix.nebula.root") version ("25.5.2")
        id("com.netflix.nebula.library") version ("25.5.2")
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

include(":archrules-common")
include(":archrules-deprecation")
include(":archrules-gradle-plugin-development")
include(":archrules-guava")
include(":archrules-javax")
include(":archrules-joda")
include(":archrules-nullability")
include(":archrules-security")
include(":archrules-testing-frameworks")
