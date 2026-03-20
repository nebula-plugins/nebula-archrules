# ArchRules Documentation

List of all archrules defined in this library.

## CVE-2018-10237

**Description:** no classes should depend on classes that simple name 'AtomicDoubleArray' or should access target where target assignable to simple name 'AtomicDoubleArray' or should depend on classes that simple name 'CompoundOrdering' or should access target where target assignable to simple name 'CompoundOrdering', because Unbounded memory allocation in Google Guava 11.0 through 24.x before 24.1.1 allows remote attackers to conduct denial of service attacks against servers that depend on this library and deserialize attacker-provided data, because the AtomicDoubleArray class (when serialized with Java serialization) and the CompoundOrdering class (when serialized with GWT serialization) perform eager allocation without appropriate checks on what a client has sent and whether the data size is reasonable. 

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.security.CveArchRules`

---

## CVE-2020-29582

**Description:** no classes should call method FilesKt.createTempDir(String, String, File) or should call method FilesKt.createTempFile(String, String, File), because A Kotlin application using createTempDir or createTempFile and placing sensitive information within either of these locations would be leaking this information in a read-only way to other users also on this system. We recommend migrating to the Java 7 API java.nio.file.Files.createTempDirectory() which explicitly configures permissions of 700

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.security.CveArchRules`

---

## CVE-2020-8908

**Description:** no classes should call method Files.createTempDir(), because A temp directory creation vulnerability exists in all versions of Guava, allowing an attacker with access to the machine to potentially access data in a temporary directory created by the Guava API com.google.common.io.Files.createTempDir(). By default, on unix-like systems, the created directory is world-readable (readable by an attacker with access to the system). We recommend migrating to the Java 7 API java.nio.file.Files.createTempDirectory() which explicitly configures permissions of 700

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.security.CveArchRules`

---

## CVE-2023-2976

**Description:** no classes should depend on classes that simple name 'FileBackedOutputStream' or should access target where target assignable to simple name 'FileBackedOutputStream', because CVE-2023-2976: Use of Java's default temporary directory for file creation in `FileBackedOutputStream` in Google Guava versions 1.0 to 31.1 on Unix systems and Android Ice Cream Sandwich allows other users and apps on the machine with access to the default Java temporary directory to be able to access the files created by the class.

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.security.CveArchRules`

---

## CVE-2024-6763

**Description:** no classes should depend on classes that have fully qualified name 'org.eclipse.jetty.http.HttpURI', because The HttpURI class does insufficient validation on the authority segment of a URI.

**Priority:** HIGH

**Class:** `com.netflix.nebula.archrules.security.CveArchRules`

---

