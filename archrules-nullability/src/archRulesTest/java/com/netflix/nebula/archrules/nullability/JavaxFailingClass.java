package com.netflix.nebula.archrules.nullability;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class JavaxFailingClass {
    @javax.annotation.Nullable
    String nullable;
    @javax.annotation.Nonnull
    String nonNullable = "";
}
