# ArchRules Documentation

List of all archrules defined in this library.

## junit4Rule

**Description:** No code should use JUnit4 test packages, because usage of JUnit4 is deprecated. Please migrate to JUnit5 Jupiter.

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.testingframeworks.JUnit4Rule`

---

## testcontainers1x-containerIpAddressMethod

**Description:** No code should use getContainerIpAddress() method, because getContainerIpAddress() was replaced with getHost() in Testcontainers 2.x. Replace calls with getHost()

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.testingframeworks.Testcontainers1xRule`

---

## testcontainers1x-dockerComposeContainer

**Description:** No code should use DockerComposeContainer, because DockerComposeContainer was renamed to ComposeContainer in Testcontainers 2.x. Update imports to use org.testcontainers.containers.ComposeContainer

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.testingframeworks.Testcontainers1xRule`

---

## testcontainers1x-noArgConstructor

**Description:** No code should use no-argument constructors on Testcontainers container classes, because Containers should specify explicit images with versions for reproducibility. Use explicit image specifications (e.g., new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine")))

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.testingframeworks.Testcontainers1xRule`

---

## testcontainers2x-legacyContainerPackage

**Description:** Code should use module-specific Testcontainers packages instead of org.testcontainers.containers, because In Testcontainers 2.x, container classes moved to module-specific packages (e.g., PostgreSQLContainer → org.testcontainers.postgresql.PostgreSQLContainer). The old locations are deprecated compatibility shims.

**Priority:** LOW

**Class:** `com.netflix.nebula.archrules.testingframeworks.Testcontainers2xRule`

---

