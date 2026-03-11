plugins {
    id("com.netflix.nebula.oss.settings") version("25.+")
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
