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

import com.google.protobuf.Any;

import static io.spine.protobuf.TypeConverter.toAny;

/**
 * Validates fields of number types (protobuf: int32, double, etc).
 *
 * @param <V>
 *         the type of the field value
 */
abstract class NumberFieldValidator<V extends Number & Comparable<V>> extends FieldValidator<V> {

    /**
     * Creates a new validator instance.
     *
     * @param fieldValue
     *         the value to validate
     */
    NumberFieldValidator(FieldValue<V> fieldValue) {
        super(fieldValue, false);
    }

    /** Converts a string representation to a number. */
    protected abstract V toNumber(String value);

    /** Returns an absolute value of the number. */
    protected abstract V getAbs(V number);

    /**
     * Wraps a value to a corresponding message wrapper
     * ({@link com.google.protobuf.DoubleValue DoubleValue},
     * {@link com.google.protobuf.Int32Value Int32Value}, etc) and {@link Any}.
     */
    Any wrap(V value) {
        Any result = toAny(value);
        return result;
    }

    /**
     * Returns {@code false}.
     *
     * <p>There's no way to define whether a Protobuf numeric field is {@code 0} or not set.
     */
    @Override
    protected boolean isNotSet(V value) {
        return false;
    }

}
