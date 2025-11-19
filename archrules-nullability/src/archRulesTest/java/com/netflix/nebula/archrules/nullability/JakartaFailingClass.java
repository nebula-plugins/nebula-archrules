package com.netflix.nebula.archrules.nullability;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class JakartaFailingClass {
    @jakarta.annotation.Nullable
    String nullable;

    @jakarta.annotation.Nonnull
    String nonNullable;
}
