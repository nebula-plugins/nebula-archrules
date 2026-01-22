package com.netflix.nebula.archrules.nullability;

import java.util.Optional;

public class OptionalFailingClass {
    private Optional<String> string;

    public static void method(Optional<String> string) { }
}
