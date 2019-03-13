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

package io.spine.tools.protoc;

import org.checkerframework.checker.signature.qual.FullyQualifiedName;

import static io.spine.util.Preconditions2.checkNotEmptyOrBlank;
import static io.spine.validate.Validate.checkNotDefault;

/**
 * An utility for working with {@link UuidConfig} and {@link ConfigByPattern} code generation task
 * configurations..
 */
public final class ProtocTaskConfigs {

    /** Prevents instantiation of this utility class. */
    private ProtocTaskConfigs() {
    }

    /**
     * Creates a new {@link UuidConfig} from the supplied {@code value}.
     *
     * @throws IllegalArgumentException
     *         if the value is blank
     * @throws NullPointerException
     *         if the value is {@code null}
     */
    public static UuidConfig uuidConfig(@FullyQualifiedName String value) {
        checkNotEmptyOrBlank(value);
        return UuidConfig
                .newBuilder()
                .setValue(value)
                .build();
    }

    public static ConfigByPattern
    byPatternConfig(@FullyQualifiedName String value, FilePattern pattern) {
        checkNotEmptyOrBlank(value);
        checkNotDefault(pattern);
        return ConfigByPattern
                .newBuilder()
                .setValue(value)
                .setPattern(pattern)
                .build();
    }
}