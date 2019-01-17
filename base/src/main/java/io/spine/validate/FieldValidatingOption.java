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

import com.google.common.collect.ImmutableList;
import io.spine.code.proto.FieldDeclaration;

import java.util.List;

/**
 * An option that validates a field.
 *
 * @param <T>
 *         type of information that this option holds
 */
abstract class FieldValidatingOption<T> implements ValidatingOption<T, FieldValue> {

    /**
     * Defines whether this option is applicable to the specified field.
     *
     * <p>Example: a {@link io.spine.option.MaxOption max option} is applicable to
     * numeric fields only.
     *
     * @param field
     *         a field applicability to which is checked
     * @return {@code true} if this option can be applied to the specified field, {@code false}
     *         otherwise
     */
    abstract boolean applicableTo(FieldDeclaration field);

    /**
     * Returns the exception that is thrown if this option was found to be inapplicable to the
     * specified field.
     */
    abstract OptionInapplicableException onInapplicable(FieldDeclaration declaration);

    /**
     * Defines the logic for validation of the specified field.
     *
     * @param value
     *         field that is being validated
     * @return a list of constraint violations, if any were found when validating the specified
     *         field
     */
    abstract List<ConstraintViolation> applyValidatingRules(FieldValue value);

    /** Returns {@code true} if this option exists for the specified field, {@code false} otherwise. */
    abstract boolean optionPresentAt(FieldValue value);

    @Override
    public final List<ConstraintViolation> validateAgainst(FieldValue value) {
        FieldDeclaration declaration = value.declaration();
        if (optionPresentAt(value)) {
            if (applicableTo(declaration)) {
                return applyValidatingRules(value);
            } else {
                throw onInapplicable(declaration);
            }
        }
        return ImmutableList.of();
    }
}
