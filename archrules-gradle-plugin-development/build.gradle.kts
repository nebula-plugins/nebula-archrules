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
    archRulesTestImplementation(gradleKotlinDsl())
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

// some of the tests use code compiled on java 17, so we need to compile and run our tests on java 17
testing {
    suites {
        named("test", JvmTestSuite::class) {
            targets.configureEach {
                testTask.configure {
                    javaLauncher = javaToolchains.launcherFor {
                        languageVersion.set(JavaLanguageVersion.of(17))
                    }
                }
            }
        }
    }
}
tasks.named<JavaCompile>("compileArchRulesTestJava") {
    javaCompiler = javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencyLocking {
    lockAllConfigurations()
}
