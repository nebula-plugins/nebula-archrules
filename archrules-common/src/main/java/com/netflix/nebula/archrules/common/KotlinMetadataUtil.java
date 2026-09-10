package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.domain.JavaTypeVariable;
import kotlin.metadata.Attributes;
import kotlin.metadata.KmClassifier;
import kotlin.metadata.KmFunction;
import kotlin.metadata.KmType;
import kotlin.metadata.KmValueParameter;
import kotlin.metadata.jvm.JvmExtensionsKt;
import kotlin.metadata.jvm.JvmMethodSignature;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public class KotlinMetadataUtil {
    private KotlinMetadataUtil() {
    }

    private static final Map<String, String> kotlinTypeConversion = Stream.of(
                    Arrays.asList("java.lang.String", "kotlin.String"),
                    Arrays.asList("java.lang.Integer", "kotlin.Int"),
                    Arrays.asList("int", "kotlin.Int"))
            .collect(Collectors.toMap(k -> k.get(0), v -> v.get(1)));

    static boolean typeMatches(JavaType type, String kotlinMetadataType) {
        String dotFormat = kotlinMetadataType.replace("/", ".");
        if (type.toErasure().getFullName().equals(dotFormat)) {
            return true;
        } else {
            if (kotlinTypeConversion.containsKey(type.toErasure().getFullName())) {
                return dotFormat.equals(kotlinTypeConversion.get(type.toErasure().getFullName()));
            } else {
                return false;
            }
        }
    }

    static boolean matchParameterList(
            com.tngtech.archunit.core.domain.JavaMethod javaMethod,
            KmFunction function) {
        List<KmValueParameter> parameters = function.getValueParameters();
        if (parameters.size() >= javaMethod.getParameters().size()) {
            int javaIndex = 0;
            int kotlinIndex = 0;
            while (javaIndex < javaMethod.getParameters().size()) {
                if (!matchParameter(javaIndex, kotlinIndex, javaMethod, parameters)) {
                    // we can skip kotlin parameters with default values
                    boolean matchFound = false;
                    while (kotlinIndex < parameters.size() && Attributes.getDeclaresDefaultValue(parameters.get(kotlinIndex))) {
                        kotlinIndex++;
                        if (matchParameter(javaIndex, kotlinIndex, javaMethod, parameters)) {
                            matchFound = true;
                            break;
                        }
                    }
                    if (!matchFound) {
                        return false;
                    }
                }
                kotlinIndex++;
                javaIndex++;
            }
            return true;
        } else {
            return false;
        }
    }

    static boolean matchParameter(int javaIndex, int kotlinIndex,
                                  com.tngtech.archunit.core.domain.JavaMethod javaMethod,
                                  List<KmValueParameter> parameters) {
        KmType kotlinType = parameters.get(kotlinIndex).type;
        JavaType javaType = javaMethod.getParameters().get(javaIndex).getType();
        if (javaType instanceof JavaTypeVariable) {
            return kotlinType.classifier instanceof KmClassifier.TypeParameter;
        } else if (javaType instanceof com.tngtech.archunit.core.domain.JavaClass) {
            if (!(kotlinType.classifier instanceof KmClassifier.Class)) {
                return false;
            }
            return typeMatches(javaType, ((KmClassifier.Class) kotlinType.classifier).getName());
        } else {
            return false;
        }
    }

    static Optional<KmFunction> matchFunction(
            com.tngtech.archunit.core.domain.JavaMethod javaMethod,
            List<KmFunction> functions) {
        List<KmFunction> nameMatches = functions.stream()
                .filter(it -> {
                    JvmMethodSignature signature = JvmExtensionsKt.getSignature(it);
                    return signature != null && signature.getName().equals(javaMethod.getName());
                }).collect(Collectors.toList());
        if (nameMatches.size() == 1) {
            return nameMatches.stream().findFirst();
        }
        return nameMatches.stream()
                .filter(it -> matchParameterList(javaMethod, it))
                .findFirst();
    }
}
