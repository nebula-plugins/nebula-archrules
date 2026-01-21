plugins {
    id("com.netflix.nebula.root")
}
tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "9.2.1"
    distributionSha256Sum = "72f44c9f8ebcb1af43838f45ee5c4aa9c5444898b3468ab3f4af7b6076c5bc3f"
}
dependencyLocking {
    lockAllConfigurations()
}
contacts {
    addPerson("nebula-plugins-oss@netflix.com") {
        moniker = "Nebula Plugins Maintainers"
        github = "nebula-plugins"
    }
}

// the following is to avoid project dependency substitutions in archrules runtimes (which breaks lock state)
subprojects {
    plugins.withId("com.netflix.nebula.archrules.runner") {
        configurations.named("testArchRulesRuntime").configure {
            resolutionStrategy.dependencySubstitution {
                // workaround for classpath issue
                all {
                    val requestedProject = requested
                    if (requestedProject is ProjectComponentSelector) {
                        useTarget("com.netflix.nebula" + requestedProject.projectPath + ":latest.release")
                    }
                }
            }
        }
        configurations.named("mainArchRulesRuntime").configure {
            resolutionStrategy.dependencySubstitution {
                // workaround for classpath issue
                all {
                    val requestedProject = requested
                    if (requestedProject is ProjectComponentSelector) {
                        useTarget("com.netflix.nebula" + requestedProject.projectPath + ":latest.release")
                    }
                }
            }
        }
        plugins.withId("com.netflix.nebula.archrules.library") {
            configurations.named("archRulesArchRulesRuntime").configure {
                resolutionStrategy.dependencySubstitution {
                    // workaround for classpath issue
                    all {
                        val requestedProject = requested
                        if (requestedProject is ProjectComponentSelector) {
                            useTarget("com.netflix.nebula" + requestedProject.projectPath + ":latest.release")
                        }
                    }
                }
            }
            configurations.named("archRulesTestArchRulesRuntime").configure {
                resolutionStrategy.dependencySubstitution {
                    // workaround for classpath issue
                    all {
                        val requestedProject = requested
                        if (requestedProject is ProjectComponentSelector) {
                            useTarget("com.netflix.nebula" + requestedProject.projectPath + ":latest.release")
                        }
                    }
                }
            }

            // avoid self-referencing dependencies
            configurations.named("archRulesTestArchRulesRuntime"){
                resolutionStrategy {
                    exclude(module = this@subprojects.name)
                }
            }
            configurations.named("archRulesTestRuntimeClasspath"){
                resolutionStrategy {
                    exclude(module = this@subprojects.name)
                }
            }
            configurations.named("archRulesTestCompileClasspath"){
                resolutionStrategy {
                    exclude(module = this@subprojects.name)
                }
            }
        }
    }
}
