package com.netflix.nebula.archrules.gradleplugins;

import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared constants for type names and annotations used across Gradle plugin architecture rules.
 */
@NullMarked
final class TypeConstants {

    private TypeConstants() {
    }

    // Java type constants
    static final String JAVA_IO_FILE = "java.io.File";
    static final String JAVA_LANG_STRING = "java.lang.String";
    static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    static final String JAVA_LANG_LONG = "java.lang.Long";
    static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
    static final String JAVA_LANG_DOUBLE = "java.lang.Double";
    static final String JAVA_LANG_FLOAT = "java.lang.Float";
    static final String JAVA_UTIL_LIST = "java.util.List";
    static final String JAVA_UTIL_SET = "java.util.Set";
    static final String JAVA_UTIL_MAP = "java.util.Map";

    // Type sets for Provider API requirements

    /**
     * Basic types that should use Provider API in plugin extensions.
     * Excludes numeric types beyond basic primitives (no Double/Float) and File types.
     * These types are commonly used in extension configuration and should be wrapped
     * in Property&lt;T&gt; for lazy configuration support.
     */
    static final Set<String> EXTENSION_TYPES_REQUIRING_PROVIDER = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    JAVA_LANG_STRING,
                    JAVA_LANG_INTEGER,
                    JAVA_LANG_LONG,
                    JAVA_LANG_BOOLEAN,
                    JAVA_UTIL_LIST,
                    JAVA_UTIL_SET
            ))
    );

    /**
     * All mutable types that should use Provider API in Gradle tasks.
     * Includes all numeric types, File, and collections. Task inputs and outputs
     * should use Provider API types for proper up-to-date checking and lazy evaluation.
     */
    static final Set<String> TASK_TYPES_REQUIRING_PROVIDER = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    JAVA_LANG_STRING,
                    JAVA_LANG_INTEGER,
                    JAVA_LANG_LONG,
                    JAVA_LANG_BOOLEAN,
                    JAVA_LANG_DOUBLE,
                    JAVA_LANG_FLOAT,
                    JAVA_IO_FILE,
                    JAVA_UTIL_LIST,
                    JAVA_UTIL_SET,
                    JAVA_UTIL_MAP
            ))
    );

    // Gradle core types
    static final String GRADLE_PLUGIN = "org.gradle.api.Plugin";
    static final String GRADLE_PROJECT = "org.gradle.api.Project";

    // Gradle task input annotations
    static final String ANNOTATION_INPUT = "org.gradle.api.tasks.Input";
    static final String ANNOTATION_INPUT_FILE = "org.gradle.api.tasks.InputFile";
    static final String ANNOTATION_INPUT_FILES = "org.gradle.api.tasks.InputFiles";
    static final String ANNOTATION_INPUT_DIRECTORY = "org.gradle.api.tasks.InputDirectory";

    // Gradle task output annotations
    static final String ANNOTATION_OUTPUT_FILE = "org.gradle.api.tasks.OutputFile";
    static final String ANNOTATION_OUTPUT_FILES = "org.gradle.api.tasks.OutputFiles";
    static final String ANNOTATION_OUTPUT_DIRECTORY = "org.gradle.api.tasks.OutputDirectory";
    static final String ANNOTATION_OUTPUT_DIRECTORIES = "org.gradle.api.tasks.OutputDirectories";

    // Gradle task cacheability annotations
    static final String ANNOTATION_CACHEABLE_TASK = "org.gradle.api.tasks.CacheableTask";
    static final String ANNOTATION_PATH_SENSITIVE = "org.gradle.api.tasks.PathSensitive";

    // Annotation sets

    /**
     * All input and output annotations for Gradle tasks.
     * Used to identify task properties that participate in up-to-date checking.
     */
    static final Set<String> INPUT_OUTPUT_ANNOTATIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    ANNOTATION_INPUT,
                    ANNOTATION_INPUT_FILE,
                    ANNOTATION_INPUT_FILES,
                    ANNOTATION_INPUT_DIRECTORY,
                    ANNOTATION_OUTPUT_FILE,
                    ANNOTATION_OUTPUT_FILES,
                    ANNOTATION_OUTPUT_DIRECTORY,
                    ANNOTATION_OUTPUT_DIRECTORIES
            ))
    );

    // Provider API recommendation strings (for error messages)
    static final String RECOMMENDATION_REGULAR_FILE_PROPERTY = "RegularFileProperty";
    static final String RECOMMENDATION_DIRECTORY_PROPERTY = "DirectoryProperty";
    static final String RECOMMENDATION_LIST_PROPERTY = "ListProperty<T>";
    static final String RECOMMENDATION_SET_PROPERTY = "SetProperty<T>";
    static final String RECOMMENDATION_MAP_PROPERTY = "MapProperty<K, V>";
}
