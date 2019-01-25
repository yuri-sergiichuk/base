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

package io.spine.code.proto;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.option.OptionsProto.by;
import static io.spine.util.Exceptions.newIllegalArgumentException;
import static java.util.regex.Pattern.compile;

/**
 * Obtains source type names from the {@code "by"} field option of a message.
 */
public final class ByOption {

    /**
     * Separates two or more alternative references.
     */
    private static final String PIPE_SEPARATOR = "|";
    private static final Pattern PATTERN_PIPE_SEPARATOR = compile("\\|");

    /** Prevents instantiation of this utility class. */
    private ByOption() {
    }

    /**
     * Verifies if the {@code (by)} option is set in the passed field.
     */
    public static boolean isSetFor(FieldDescriptorProto field) {
        return valueIn(field).isPresent();
    }

    /**
     * Obtains the value of the {@code (by)} option in the passed field.
     */
    public static Optional<String> valueIn(FieldDescriptorProto field) {
        String value = field.getOptions()
                            .getExtension(by);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    static ImmutableList<FieldReference> allFrom(FieldDescriptorProto field) {
        checkNotNull(field);
        String[] found = parse(field);

        ImmutableList.Builder<FieldReference> result = ImmutableList.builder();
        for (String ref : found) {
            result.add(new FieldReference(ref));
        }
        return result.build();
    }

    private static String[] parse(FieldDescriptorProto field) {
        String byArgument = valueIn(field)
                .orElseThrow(() -> missingOptionIn(field));
        String[] result;
        result = byArgument.contains(PIPE_SEPARATOR)
                 ? PATTERN_PIPE_SEPARATOR.split(byArgument)
                 : new String[]{byArgument};
        return result;
    }

    static IllegalArgumentException missingOptionIn(FieldDescriptorProto field) {
        return newIllegalArgumentException(
                "There is no `by` option in the field `%s`.",
                field.getName());
    }
}
