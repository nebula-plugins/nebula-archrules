# Contributing

## Adding a Rule to Existing Rule Library

1. Implement your rule class in the appropriate subproject under `src/archRules/java/`
2. Add a corresponding test in `src/archRulesTest/java/`
3. Regenerate the documentation for the subproject:
   ```
   ./gradlew :<subproject>:generateRulesDocumentation
   ```
   For example:
   ```
   ./gradlew :archrules-deprecation:generateRulesDocumentation
   ```
4. Copy the contents of `<subproject>/build/docs/archrules.md` into the subproject's `README.md`, replacing any existing rule documentation.

## Adding a New Rule Library

1. Create a new subproject directory and register it in `settings.gradle.kts`.
2. Apply the `com.netflix.nebula.archrules.library` plugin in the subproject's `build.gradle.kts`.
3. Follow the steps above to implement rules and generate documentation.
4. Add the new subproject to the root `README.md` under the **ArchRule Libraries** section.
