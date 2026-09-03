package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.examples.PublicKotlinClass;
import kotlin.Metadata;
import kotlin.metadata.KmFunction;
import kotlin.metadata.KmValueParameter;
import kotlin.metadata.jvm.JvmExtensionsKt;
import kotlin.metadata.jvm.KotlinClassMetadata;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class KotlinMetadataUtilTest {
    @Test
    public void test_indicesOfOptionalArgs() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParams"))
                .findFirst();
        List<Set<Integer>> actual = KotlinMetadataUtil.indicesOfOptionalArgs(function.get().getValueParameters());
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isEmpty();
        assertThat(actual.get(1)).containsExactly(1);
    }

    @Test
    public void test_expandKotlinFunction() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParams"))
                .findFirst();
        List<List<KmValueParameter>> actual = KotlinMetadataUtil.expandKotlinFunction(function.get());
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).hasSize(2);
        assertThat(actual.get(1)).hasSize(1);
    }

    @Test
    public void test_matchParameterList() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParams"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("publicManyParams", String.class, String.class);

        assertThat(KotlinMetadataUtil.matchParameterList(scannedMethod, function.get().getValueParameters())).isTrue();
    }

    @Test
    public void matchFunction() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParams"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("publicManyParams", String.class, String.class);

        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, Collections.singletonList(function.get())))
                .hasValue(function.get());
    }
}
