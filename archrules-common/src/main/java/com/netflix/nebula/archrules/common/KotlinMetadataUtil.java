package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.domain.JavaTypeVariable;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.metadata.Attributes;
import kotlin.metadata.KmClassifier;
import kotlin.metadata.KmFunction;
import kotlin.metadata.KmType;
import kotlin.metadata.KmValueParameter;
import kotlin.metadata.jvm.JvmExtensionsKt;
import kotlin.metadata.jvm.JvmMethodSignature;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
public class KotlinMetadataUtil {
    private KotlinMetadataUtil() {
    }

    @Nullable
    static String convertPrimitiveToKotlin(String javaPrimitive) {
        switch (javaPrimitive) {
            case "java.lang.Boolean":
                return "kotlin.Boolean";
            case "boolean":
                return "kotlin.Boolean";
            case "java.lang.Character":
                return "kotlin.Char";
            case "char":
                return "kotlin.Char";
            case "java.lang.Byte":
                return "kotlin.Byte";
            case "byte":
                return "kotlin.Byte";
            case "java.lang.Short":
                return "kotlin.Short";
            case "short":
                return "kotlin.Short";
            case "java.lang.Integer":
                return "kotlin.Int";
            case "int":
                return "kotlin.Int";
            case "java.lang.Float":
                return "kotlin.Float";
            case "float":
                return "kotlin.Float";
            case "java.lang.Long":
                return "kotlin.Long";
            case "long":
                return "kotlin.Long";
            case "java.lang.Double":
                return "kotlin.Double";
            case "double":
                return "kotlin.Double";
            case "java.lang.String":
                return "kotlin.String";
            default:
                return null;
        }
    }

    static boolean typeMatches(JavaType type, String kotlinMetadataType) {
        String dotFormat = kotlinMetadataType.replace("/", ".");
        String javaFullName = type.toErasure().getFullName();
        if (javaFullName.equals(dotFormat)) {
            return true;
        } else {
            String kotlinRepresentationOfJavaPrimitive = convertPrimitiveToKotlin(javaFullName);
            return kotlinRepresentationOfJavaPrimitive != null && kotlinRepresentationOfJavaPrimitive.equals(dotFormat);
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
                        if (kotlinIndex >= parameters.size()) {
                            return false;
                        }
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
