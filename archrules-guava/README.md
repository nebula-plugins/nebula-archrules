# ArchRules Documentation

List of all archrules defined in this library.

## guava collections

**Description:** no classes should depend on classes that reside in a package 'com.google.common.collect..', because Guava collections should not be used for compatibility reasons. Prefer Java or Kotlin standard library collections instead.

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.guava.GuavaRules`

---

## guava optional

**Description:** no classes should depend on classes that have fully qualified name 'com.google.common.base.Optional', because Java Optional is preferred over Guava Optional

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.guava.GuavaRules`

---

