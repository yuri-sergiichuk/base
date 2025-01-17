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

import io.spine.logging.Logging;
import io.spine.validate.option.Constraint;
import io.spine.validate.option.RequiredField;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Validates that one of the fields defined by the {@code required_field} option is present.
 *
 * See definition of {@code MessageOptions.required_field} in {@code options.proto}.
 */
final class AlternativeFieldValidator implements Logging {

    private final MessageValue message;

    /**
     * The list builder to accumulate violations.
     */

    AlternativeFieldValidator(MessageValue message) {
        this.message = checkNotNull(message);
    }

    List<ConstraintViolation> validate() {
        RequiredField requiredFieldOption = new RequiredField();
        Constraint<MessageValue> required = requiredFieldOption.constraintFor(message);
        return required.check(message);
    }
}
