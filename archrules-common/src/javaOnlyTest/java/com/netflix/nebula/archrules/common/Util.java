package com.netflix.nebula.archrules.common;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.core.importer.Locations;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class Util {
    private static final ClassFileImporter importer = new ClassFileImporter();

    static JavaClass scanClass(Class<?> clazz) {
        return importer.importClass(clazz);
    }

    static JavaClasses scanClasses(Class<?>... classes) {
        return importer.importClasses(classes);
    }

    /**
     * workaround for https://github.com/TNG/ArchUnit/issues/1564
     * @deprecated This is only needed until https://github.com/TNG/ArchUnit/pull/1565 is merged
     */
    @Deprecated
    static JavaClasses scanClassesWithPackage(Class<?>... classes) {
        Set<Location> locs = Arrays.stream(classes)
                .map(Locations::ofClass)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<URL> uris = Arrays.stream(classes)
                .map(clazz -> clazz.getPackage().getName())
                .map(Locations::ofPackage)
                .flatMap(it -> it.stream().map(Location::asURI))
                .map(u -> URI.create(u.toASCIIString() + "package-info.class"))
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        locs.addAll(Locations.of(uris));
        return importer.importLocations(locs);
    }
}
