/*
 * Copyright 2019, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.validate;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;

import java.util.Set;

/**
 * An implementation of {@link ValidatorFactory} which adds common validation options.
 *
 * <p>Creates options:
 * <ul>
 *     <li>{@code (pattern)} for {@code string} fields;
 *     <li>{@code (max)}, {@code (min)}, {@code (range)}, and {@code (digits)} for number fields.
 * </ul>
 */
@Immutable
public final class CommonValidatorFactory implements ValidatorFactory {

    private static final ImmutableSet<FieldValidatingOption<?, String>> STRING_OPTIONS =
            ImmutableSet.of(Pattern.create());
    private static final ImmutableSet<FieldValidatingOption<?, Integer>> INT_OPTIONS =
            ImmutableSet.of(Max.create(), Min.create(), Range.create(), Digits.create());
    private static final ImmutableSet<FieldValidatingOption<?, Long>> LONG_OPTIONS =
            ImmutableSet.of(Max.create(), Min.create(), Range.create(), Digits.create());
    private static final ImmutableSet<FieldValidatingOption<?, Float>> FLOAT_OPTIONS =
            ImmutableSet.of(Max.create(), Min.create(), Range.create(), Digits.create());
    private static final ImmutableSet<FieldValidatingOption<?, Double>> DOUBLE_OPTIONS =
            ImmutableSet.of(Max.create(), Min.create(), Range.create(), Digits.create());

    @Override
    public Set<FieldValidatingOption<?, String>> optionsForString() {
        return STRING_OPTIONS;
    }

    @Override
    public Set<FieldValidatingOption<?, Integer>> optionsForInt() {
        return INT_OPTIONS;
    }

    @Override
    public Set<FieldValidatingOption<?, Long>> optionsForLong() {
        return LONG_OPTIONS;
    }

    @Override
    public Set<FieldValidatingOption<?, Float>> optionsForFloat() {
        return FLOAT_OPTIONS;
    }

    @Override
    public Set<FieldValidatingOption<?, Double>> optionsForDouble() {
        return DOUBLE_OPTIONS;
    }
}
