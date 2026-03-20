# ArchRules Documentation

List of all archrules defined in this library.

## Cacheable Task input field path sensitivity

**Description:** fields that annotated with Input file annotations and are declared in classes that are annotated with @CacheableTask should be annotated with @PathSensitive, because Cacheable tasks with file inputs must declare @PathSensitive to specify how paths affect cache keys. This ensures build cache entries are relocatable across machines. See https://docs.gradle.org/current/userguide/build_cache.html#sec:task_output_caching_inputs

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Cacheable Task input method path sensitivity

**Description:** methods that annotated with Input file annotations and are declared in classes that are annotated with @CacheableTask should be annotated with @PathSensitive, because Cacheable tasks with file inputs must declare @PathSensitive to specify how paths affect cache keys. This ensures build cache entries are relocatable across machines. See https://docs.gradle.org/current/userguide/build_cache.html#sec:task_output_caching_inputs

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Extension abstract getters

**Description:** methods that are extension property getters should have modifier ABSTRACT, because Extension property getters returning Provider API types should be abstract. This allows Gradle to generate the implementation at runtime. See https://docs.gradle.org/current/userguide/custom_plugins.html#sec:implementing_an_extension

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Extension fields use Provider API

**Description:** fields that are declared in classes that are plugin extension class and are not static and have type that should use Provider API should use Provider API type, because Plugin extension fields should use Provider API types (Property<T>, ListProperty<T>, SetProperty<T>) instead of plain mutable types. This enables lazy configuration and better integration with Gradle's configuration system. See https://docs.gradle.org/current/userguide/lazy_configuration.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Plugin should inject ObjectFactory

**Description:** no classes that implement org.gradle.api.Plugin should call method where calls getObjects on org.gradle.api.Project, because Plugins should inject ObjectFactory via constructor instead of calling project.getObjects(). Use @Inject constructor parameter for better testability and to follow Gradle best practices. Example: @Inject public MyPlugin(ObjectFactory objects) { this.objects = objects; } See https://docs.gradle.org/current/userguide/service_injection.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Plugin should inject ProviderFactory

**Description:** no classes that implement org.gradle.api.Plugin should call method where calls getProviders on org.gradle.api.Project, because Plugins should inject ProviderFactory via constructor instead of calling project.getProviders(). Use @Inject constructor parameter for better testability and to follow Gradle best practices. Example: @Inject public MyPlugin(ProviderFactory providers) { this.providers = providers; } See https://docs.gradle.org/current/userguide/service_injection.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Plugin storing Project references

**Description:** fields that are declared in classes that implement org.gradle.api.Plugin should not have raw type assignable to org.gradle.api.Project, because Plugins should not store Project references as fields. This breaks configuration cache and prevents garbage collection. Extract needed values in apply() method or use service injection instead. See https://docs.gradle.org/current/userguide/configuration_cache.html

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Plugin using deprecated gradle APIs

**Description:** no classes that implement org.gradle.api.Plugin should depend on classes that is is reside in a package 'org.gradle..' and annotated with @Deprecated or should access target where is target is is reside in a package 'org.gradle..' and annotated with @Deprecated or target annotated with @Deprecated, because Plugins should not use deprecated Gradle APIs as they will be removed in future versions. Consult Gradle upgrade guides for modern alternatives. See https://docs.gradle.org/current/userguide/upgrading_version_8.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Plugin using internal gradle APIs

**Description:** no classes that implement org.gradle.api.Plugin should depend on classes that is is reside in a package 'org.gradle..' and reside in a package '..internal..' or should access target where target is is reside in a package 'org.gradle..' and reside in a package '..internal..', because Plugins should not use internal Gradle APIs (packages containing '.internal.'). Internal APIs are not stable and may change or be removed without notice. Use only public Gradle APIs documented at https://docs.gradle.org/current/javadoc/

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Task declares inputs and/or outputs

**Description:** classes that are assignable to org.gradle.api.DefaultTask and are not interfaces and have contain any methods that are annotated with @TaskAction and do not have simple name 'DefaultTask' should contain any methods that are annotated with Input and/or Output annotations or should contain any fields that are annotated with Input and/or Output annotations, because Tasks must declare inputs and outputs using @Input, @InputFile, @InputDirectory, @Output, @OutputFile, or @OutputDirectory annotations. This is required for incremental builds and caching to work correctly. See https://docs.gradle.org/current/userguide/incremental_build.html

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Task using deprecated gradle APIs

**Description:** no classes that are assignable to org.gradle.api.Task and are not interfaces should depend on classes that is is reside in a package 'org.gradle..' and annotated with @Deprecated or should access target where is target is is reside in a package 'org.gradle..' and annotated with @Deprecated or target annotated with @Deprecated, because Tasks should not use deprecated Gradle APIs as they will be removed in future versions. Consult Gradle upgrade guides for modern alternatives. See https://docs.gradle.org/current/userguide/upgrading_version_8.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## Task using internal gradle APIs

**Description:** no classes that are assignable to org.gradle.api.Task and are not interfaces should depend on classes that is is reside in a package 'org.gradle..' and reside in a package '..internal..' or should access target where target is is reside in a package 'org.gradle..' and reside in a package '..internal..', because Tasks should not use internal Gradle APIs (packages containing '.internal.'). Internal APIs are not stable and may change or be removed without notice. Use only public Gradle APIs documented at https://docs.gradle.org/current/javadoc/

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## abstract getters

**Description:** methods that are task property getters should have modifier ABSTRACT, because task implementations should define properties as abstract getters

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## lazy task registration

**Description:** no classes that implement org.gradle.api.Plugin should call method where task is created eagerly, because Plugins should use tasks.register() instead of task() or tasks.create() for lazy task registration. Eager task creation runs during configuration phase on EVERY build, significantly impacting performance. Lazy registration with tasks.register() only creates tasks when needed. See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## provider properties

**Description:** classes that are assignable to org.gradle.api.Task and are not interfaces should use Provider API for input/output properties, because Task input/output properties should use Provider API types (Property<T>, RegularFileProperty, DirectoryProperty, ConfigurableFileCollection) instead of plain types. This enables lazy configuration and configuration avoidance, which significantly improves build performance. See https://docs.gradle.org/current/userguide/lazy_configuration.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## task dependencies

**Description:** methods that are annotated with @TaskAction should not access TaskDependency, because Calling getTaskDependencies() in @TaskAction methods breaks configuration cache and will be removed in Gradle 10. Declare task dependencies at configuration time instead. See https://docs.gradle.org/9.2.0/userguide/upgrading_version_7.html#task_dependencies

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## task project access

**Description:** methods that are annotated with @TaskAction should not access Project, because Accessing Project in @TaskAction methods breaks configuration cache and will be removed in Gradle 10. Move Project access to task configuration time and use task inputs/properties instead. See https://docs.gradle.org/9.2.0/userguide/upgrading_version_7.html#task_project

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## use configureEach instead of all

**Description:** no classes that implement org.gradle.api.Plugin should call method where calls all on org.gradle.api.DomainObjectCollection and not target assignable to org.gradle.api.artifacts.ConfigurationContainer, because Plugins should use configureEach() instead of all() for lazy task configuration. all() realizes and configures all matching tasks immediately during configuration phase. configureEach() only configures tasks when they are realized. See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

## use named instead of getByName

**Description:** no classes that implement org.gradle.api.Plugin should call method where calls getByName on any of org.gradle.api.tasks.TaskContainer, org.gradle.api.tasks.TaskCollection, because Plugins should use tasks.named() instead of tasks.getByName() for lazy task lookup. getByName() forces immediate task realization during configuration phase, impacting performance. named() returns a TaskProvider that delays task creation until needed. See https://docs.gradle.org/current/userguide/task_configuration_avoidance.html

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.gradleplugins.GradlePluginBestPractices`

---

