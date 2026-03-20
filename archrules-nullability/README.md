# ArchRules Documentation

List of all archrules defined in this library.

## no Optional class fields

**Description:** no fields should have raw type java.util.Optional, because Class fields should not be Optional, use Nullable instead

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

## no Optional method parameters

**Description:** no methods should have raw parameter types [java.util.Optional], because Method parameters should not be Optional

**Priority:** LOW

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

## public classes should be @NullMarked

**Description:** classes that are top level classes and are public and contain any members that modifier PUBLIC and have no tests and are not annotated with @Metadata should be annotated with @NullMarked, because public classes should be null marked

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

## upgrade legacy jakarta annotations

**Description:** no classes that are annotated with @NullMarked should depend on classes that fully qualified name 'jakarta.annotation.Nullable' or should depend on classes that fully qualified name 'jakarta.annotation.Nonnull', because Only JSpecify annotations should be used on @NullMarked classes

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

## upgrade legacy javax annotations

**Description:** no classes that are annotated with @NullMarked should depend on classes that fully qualified name 'javax.annotation.Nullable' or should depend on classes that fully qualified name 'javax.annotation.Nonnull', because Only JSpecify annotations should be used on @NullMarked classes

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

## upgrade legacy jetbrains annotations

**Description:** no classes that are annotated with @NullMarked should depend on classes that fully qualified name 'org.jetbrains.annotations.Nullable' or should depend on classes that fully qualified name 'org.jetbrains.annotations.NotNull', because Only JSpecify annotations should be used on @NullMarked classes

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

## upgrade legacy spring annotations

**Description:** no classes that are annotated with @NullMarked should depend on classes that fully qualified name 'org.springframework.lang.Nullable' or should depend on classes that fully qualified name 'org.springframework.lang.NonNull', because Only JSpecify annotations should be used on @NullMarked classes

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.nullability.NebulaNullabilityArchRules`

---

