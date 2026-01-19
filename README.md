# Nebula ArchRules

## ArchRule Building Blocks
This repository contains the `archrules-common` library which contains ArchUnit primitives (DescribedPredicates, ChainableFunctions, and ArchConditions) which assist in writing rules. These extend what comes out-of-the box in ArchUnit to cover more cases.

[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-common?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-common/)

## ArchRule Libraries
This repository also contains several libraries of ArchRules which can be used in projects by using the [ArchRules Runner](https://github.com/nebula-plugins/nebula-archrules-plugin?tab=readme-ov-file#running-rules) plugin.


### Deprecation Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-deprecation?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-deprecation/)

### Gradle Plugin Development
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-gradle-plugin-development?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-gradle-plugin-development/)

These rules enforce best practices when developing Gradle plugins.

### Guava Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-guava?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-guava/)

These rules detect the usage of certain APIs from Guava which have standard library replacements.

### Javax Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-joda?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-javax/)

These rules enforce the usage of `jakarta` over `javax`.

### Joda Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-joda?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-joda/)

These rules enforce the usage of `java.time` over Joda Time.

### Nullability Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-nullability?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-nullability/)

These rules enforce JSpecify nullability annotations on public code. Kotlin classes are exempt from the rule, as Kotlin has nullability built into its type system, which is compatible with JSpecify.

### Security Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-security?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-archrules-security/)

These rules ensure calls are not made to known insecure OSS Java APIs.

### Testing Frameworks Rules
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/archrules-testing-frameworks?style=for-the-badge&color=01AF01)](https://repo1.maven.org/maven2/com/netflix/nebula/archrules-testing-frameworks/)

These rules enforce upgrading to JUnit Jupiter.

## LICENSE

Copyright 2025 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
