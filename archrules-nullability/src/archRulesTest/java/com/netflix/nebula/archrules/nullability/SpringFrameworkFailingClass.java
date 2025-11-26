package com.netflix.nebula.archrules.nullability;

import org.jspecify.annotations.NullMarked;


@NullMarked
public class SpringFrameworkFailingClass {
    @org.springframework.lang.Nullable
    String nullable;

    @org.springframework.lang.NonNull
    String nonNullable = "";
}
