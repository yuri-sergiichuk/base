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

import org.checkerframework.checker.regex.qual.Regex;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An utility for working with {@link TypeFilter}.
 */
public final class TypeFilters {

    /** Prevents instantiation of this utility class. */
    private TypeFilters() {
    }

    /**
     * Creates a new {@link TypeFilter} with a {@code file_postfix} field filled.
     */
    public static TypeFilter filePostfix(@Regex String postfix) {
        checkNotNull(postfix);
        return TypeFilter.newBuilder()
                         .setFilePostfix(postfix)
                         .build();
    }

    /**
     * Creates a new {@link TypeFilter} with a {@code option_name} field filled.
     */
    public static TypeFilter optionName(String optionName) {
        checkNotNull(optionName);
        return TypeFilter.newBuilder()
                         .setOptionName(optionName)
                         .build();
    }
}
