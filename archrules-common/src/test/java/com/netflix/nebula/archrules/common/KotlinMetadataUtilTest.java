package com.netflix.nebula.archrules.common;

import com.netflix.nebula.archrules.common.examples.ClassA;
import com.netflix.nebula.archrules.common.examples.ClassB;
import com.netflix.nebula.archrules.common.examples.LargeDataClass;
import com.netflix.nebula.archrules.common.examples.PublicKotlinClass;
import kotlin.Metadata;
import kotlin.metadata.KmFunction;
import kotlin.metadata.jvm.JvmExtensionsKt;
import kotlin.metadata.jvm.KotlinClassMetadata;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class KotlinMetadataUtilTest {

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

        assertThat(KotlinMetadataUtil.matchParameterList(scannedMethod, function.get())).isTrue();
    }

    @Test
    public void test_matchParameterList_generic() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("generic"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethods().stream()
                        .filter(it -> it.getName().equals("generic")).findFirst().get();

        assertThat(KotlinMetadataUtil.matchParameterList(scannedMethod, function.get())).isTrue();
    }

    @Test
    public void test_matchParameterList_jvm_overloads() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParams"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("publicManyParams", String.class);

        assertThat(KotlinMetadataUtil.matchParameterList(scannedMethod, function.get())).isTrue();
    }

    @Test
    public void test_matchParameterList_final_default() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).toString().equals("functionWithFinalDefault(II)V"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("functionWithFinalDefault", int.class, String.class);

        assertThat(KotlinMetadataUtil.matchParameterList(scannedMethod, function.get())).isFalse();
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

        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, metadataClass.getKmClass().getFunctions()))
                .hasValue(function.get());
    }

    @Test
    public void matchFunction_jvm_overloads() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParams"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("publicManyParams", String.class);

        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, metadataClass.getKmClass().getFunctions()))
                .hasValue(function.get());
    }

    @Test
    public void matchFunction_jvm_overloads_publicManyParamsDifferentTypes() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;
        Optional<KmFunction> function = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("publicManyParamsDifferentTypes"))
                .findFirst();

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("publicManyParamsDifferentTypes", int.class, String.class);

        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, metadataClass.getKmClass().getFunctions()))
                .hasValue(function.get());
    }

    @Test
    public void matchFunction_jvm_overloads_defaultParams_different_types() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("defaultParams", ClassA.class, int.class);

        Optional<KmFunction> expected = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).toString().equals("defaultParams(Lcom/netflix/nebula/archrules/common/examples/ClassA;Ljava/lang/String;I)V"))
                .findFirst();
        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, metadataClass.getKmClass().getFunctions()))
                .hasValue(expected.get());
    }


    @Test
    public void matchFunction_jvm_overloads_defaultParams_same_types() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(PublicKotlinClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(PublicKotlinClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethod("defaultParams", ClassB.class, String.class);

        Optional<KmFunction> expected = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).toString().equals("defaultParams(Lcom/netflix/nebula/archrules/common/examples/ClassB;Ljava/lang/String;Ljava/lang/String;)V"))
                .findFirst();
        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, metadataClass.getKmClass().getFunctions()))
                .hasValue(expected.get());
    }

    @Test
    public void matchFunction_large() {
        KotlinClassMetadata metadata = KotlinClassMetadata.readStrict(LargeDataClass.class.getAnnotation(Metadata.class));
        KotlinClassMetadata.Class metadataClass = (KotlinClassMetadata.Class) metadata;

        com.tngtech.archunit.core.domain.JavaClass scannedClass = Util.scanClass(LargeDataClass.class);
        com.tngtech.archunit.core.domain.JavaMethod scannedMethod =
                scannedClass.getMethods().stream().filter(it -> it.getName().equals("copy"))
                        .findFirst()
                        .get();

        Optional<KmFunction> expected = metadataClass.getKmClass().getFunctions().stream()
                .filter(f -> JvmExtensionsKt.getSignature(f).getName().equals("copy"))
                .findFirst();
        assertThat(KotlinMetadataUtil.matchFunction(scannedMethod, metadataClass.getKmClass().getFunctions()))
                .hasValue(expected.get());
    }
}
