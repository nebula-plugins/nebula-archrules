# ArchRules Documentation

List of all archrules defined in this library.

## deprecated

**Description:** no classes should have any dependencies that do not reside in same package and target is deprecated or should access target where not in the same package and target is deprecated or target is deprecated, because usage of deprecated APIs introduces risk that future upgrades and migrations will be blocked

**Priority:** LOW

**Class:** `com.netflix.nebula.archrules.deprecation.DeprecationRule`

---

## deprecatedForRemoval

**Description:** no classes should have any dependencies that do not reside in same package and target is deprecated for removal or should access target where not in the same package and target is deprecated for removal or target is deprecated for removal, because these APIs are scheduled for removal and usage will block future upgrades

**Priority:** MEDIUM

**Class:** `com.netflix.nebula.archrules.deprecation.DeprecationRule`

---

