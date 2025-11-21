# Nebula ArchRules

This repository contains several libraries of ArchRules which can be used in projects by using the [ArchRules Runner](https://github.com/nebula-plugins/nebula-archrules-plugin?tab=readme-ov-file#running-rules) plugin.


## Deprecation Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-deprecation?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-deprecation/)

## Joda Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-joda?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-joda/)
These rules enforce the usage of `java.time` over Joda Time

## Testing Frameworks Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-testing-frameworks?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-testing-frameworks/)
These rules enforce upgrading to JUnit Jupiter

## Nullability Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-nullability?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-nullability/)
These rules enforce JSpecify nullability annotations on public code. Kotlin classes are exempt from the rule, as Kotlin has nullability built into its type system, which is compatible with JSpecify.

## LICENSE

Copyright 2025 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.