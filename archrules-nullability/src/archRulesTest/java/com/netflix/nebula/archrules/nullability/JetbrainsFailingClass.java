package com.netflix.nebula.archrules.nullability;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class JetbrainsFailingClass {
    @org.jetbrains.annotations.Nullable
    String nullable;

    @org.jetbrains.annotations.NotNull
    String nonNullable = "";
}
