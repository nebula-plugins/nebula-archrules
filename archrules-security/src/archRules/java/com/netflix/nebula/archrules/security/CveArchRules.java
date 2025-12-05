package com.netflix.nebula.archrules.security;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;

@NullMarked
public class CveArchRules implements ArchRulesService {
    public static final ArchRule CVE_2020_29582 = ArchRuleDefinition.priority(Priority.HIGH)
            .noClasses()
            .should().callMethod(
                    "kotlin.io.FilesKt",
                    "createTempDir",
                    "java.lang.String", "java.lang.String", "java.io.File")
            .orShould().callMethod(
                    "kotlin.io.FilesKt",
                    "createTempFile",
                    "java.lang.String", "java.lang.String", "java.io.File")
            .because("A Kotlin application using createTempDir or createTempFile " +
                     "and placing sensitive information within either of these locations " +
                     "would be leaking this information in a read-only way to other users also on this system. " +
                     "We recommend migrating to the Java 7 API java.nio.file.Files.createTempDirectory() " +
                     "which explicitly configures permissions of 700");

    public static final ArchRule CVE_2023_2976 = ArchRuleDefinition.priority(Priority.HIGH)
            .noClasses()
            .should()
            .dependOnClassesThat(JavaClass.Predicates.simpleName("FileBackedOutputStream"))
            .orShould()
            .accessTargetWhere(targetOwner(assignableTo(JavaClass.Predicates.simpleName("FileBackedOutputStream"))))
            .because("CVE-2023-2976: Use of Java's default temporary directory for file creation in " +
                     "`FileBackedOutputStream` in Google Guava versions 1.0 to 31.1 on Unix systems " +
                     "and Android Ice Cream Sandwich allows other users and apps on the machine " +
                     "with access to the default Java temporary directory to be able to access the " +
                     "files created by the class.");

    public static final ArchRule CVE_2020_8908 = ArchRuleDefinition.priority(Priority.HIGH)
            .noClasses()
            .should().callMethod("com.google.common.io.Files", "createTempDir")
            .because("A temp directory creation vulnerability exists in all versions of Guava, " +
                     "allowing an attacker with access to the machine to potentially access data in a temporary directory " +
                     "created by the Guava API com.google.common.io.Files.createTempDir(). " +
                     "By default, on unix-like systems, the created directory is world-readable " +
                     "(readable by an attacker with access to the system). " +
                     "We recommend migrating to the Java 7 API java.nio.file.Files.createTempDirectory() " +
                     "which explicitly configures permissions of 700");

    public static final ArchRule CVE_2018_10237 = ArchRuleDefinition.priority(Priority.HIGH)
            .noClasses()
            .should()
            .dependOnClassesThat(JavaClass.Predicates.simpleName("AtomicDoubleArray"))
            .orShould()
            .accessTargetWhere(targetOwner(assignableTo(JavaClass.Predicates.simpleName("AtomicDoubleArray"))))
            .orShould()
            .dependOnClassesThat(JavaClass.Predicates.simpleName("CompoundOrdering"))
            .orShould()
            .accessTargetWhere(targetOwner(assignableTo(JavaClass.Predicates.simpleName("CompoundOrdering"))))
            .because("Unbounded memory allocation in Google Guava 11.0 through 24.x before 24.1.1 " +
                     "allows remote attackers to conduct denial of service attacks against servers " +
                     "that depend on this library and deserialize attacker-provided data, " +
                     "because the AtomicDoubleArray class (when serialized with Java serialization) " +
                     "and the CompoundOrdering class (when serialized with GWT serialization) " +
                     "perform eager allocation without appropriate checks on what a client has sent " +
                     "and whether the data size is reasonable. ");

    public static final ArchRule CVE_2024_6763 = ArchRuleDefinition.priority(Priority.HIGH)
            .noClasses()
            .should().dependOnClassesThat().haveFullyQualifiedName("org.eclipse.jetty.http.HttpURI")
            .because("The HttpURI class does insufficient validation on the authority segment of a URI.");

    @Override
    public Map<String, ArchRule> getRules() {
        Map<String, ArchRule> rules = new HashMap<>();
        rules.put("CVE-2020-29582", CVE_2020_29582);
        rules.put("CVE-2023-2976", CVE_2023_2976);
        rules.put("CVE-2020-8908", CVE_2020_8908);
        rules.put("CVE-2018-10237", CVE_2018_10237);
        rules.put("CVE-2024-6763", CVE_2024_6763);
        return rules;
    }
}
