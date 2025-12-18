package com.netflix.nebula.archrules.gradleplugins;

import org.jspecify.annotations.NullMarked;

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

    // Provider API recommendation strings (for error messages)
    static final String RECOMMENDATION_REGULAR_FILE_PROPERTY = "RegularFileProperty";
    static final String RECOMMENDATION_DIRECTORY_PROPERTY = "DirectoryProperty";
    static final String RECOMMENDATION_LIST_PROPERTY = "ListProperty<T>";
    static final String RECOMMENDATION_SET_PROPERTY = "SetProperty<T>";
    static final String RECOMMENDATION_MAP_PROPERTY = "MapProperty<K, V>";
}
