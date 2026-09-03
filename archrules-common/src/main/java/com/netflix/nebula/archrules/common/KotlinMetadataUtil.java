package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.core.domain.JavaType;
import kotlin.metadata.Attributes;
import kotlin.metadata.KmClassifier;
import kotlin.metadata.KmFunction;
import kotlin.metadata.KmType;
import kotlin.metadata.KmValueParameter;
import kotlin.metadata.jvm.JvmExtensionsKt;
import kotlin.metadata.jvm.JvmMethodSignature;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@NullMarked
public class KotlinMetadataUtil {
    private KotlinMetadataUtil() {
    }

    static List<Set<Integer>> indicesOfOptionalArgs(List<KmValueParameter> parameters) {
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(Collections.emptySet());
        for (int i = 0; i < parameters.size(); i++) {
            if (Attributes.getDeclaresDefaultValue(parameters.get(i))) {
                final int index = i;
                List<Set<Integer>> newSets = new ArrayList<>();
                for (Set<Integer> set : sets) {
                    Set<Integer> newSet = new HashSet<>(set);
                    newSet.add(index);
                    newSets.add(newSet);
                }
                sets.addAll(newSets);
            }
        }
        return sets;
    }

    static List<List<KmValueParameter>> expandKotlinFunction(KmFunction kotlinFunction) {
        List<List<KmValueParameter>> list = new ArrayList<>();
        List<Set<Integer>> indicesOfOptionalArgs = indicesOfOptionalArgs(kotlinFunction.getValueParameters());
        indicesOfOptionalArgs.forEach(argList -> {
            List<KmValueParameter> filteredList = new ArrayList<>();
            for (int i = 0; i < kotlinFunction.getValueParameters().size(); i++) {
                if (!argList.contains(i)) {
                    filteredList.add(kotlinFunction.getValueParameters().get(i));
                }
            }
            list.add(filteredList);
        });
        return list;
    }

    private static final Map<String, String> kotlinTypeConversion =
            Collections.singletonMap("kotlin.String", "java.lang.String");

    static boolean typeMatches(JavaType type, String kotlinMetadataType) {
        String dotFormat = kotlinMetadataType.replace("/", ".");
        if (type.toErasure().toString().equals(dotFormat)) {
            return true;
        } else {
            if (kotlinTypeConversion.containsKey(dotFormat)) {
                return type.toErasure().getFullName().equals(kotlinTypeConversion.get(dotFormat));
            } else {
                return false;
            }
        }
    }

    static boolean matchParameterList(
            com.tngtech.archunit.core.domain.JavaMethod javaMethod,
            List<KmValueParameter> parameters) {
        if (parameters.size() == javaMethod.getParameters().size()) {
            for (int i = 0; i < parameters.size(); i++) {
                KmType kotlinType = parameters.get(i).type;
                if (!typeMatches(javaMethod.getParameterTypes().get(i), ((KmClassifier.Class) kotlinType.classifier).getName())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    static Optional<KmFunction> matchFunction(
            com.tngtech.archunit.core.domain.JavaMethod javaMethod,
            List<KmFunction> functions) {
        return functions.stream()
                .filter(it -> {
                    JvmMethodSignature signature = JvmExtensionsKt.getSignature(it);
                    return signature != null && signature.getName().equals(javaMethod.getName());
                })
                .filter(kmFunction ->
                        expandKotlinFunction(kmFunction).stream()
                                .anyMatch(it ->
                                        matchParameterList(javaMethod, it))

                ).findFirst();
    }
}
