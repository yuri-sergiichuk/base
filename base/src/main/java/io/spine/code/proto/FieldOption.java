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

import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import io.spine.validate.FieldValue;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Protobuf option that is applied to fields in Protobuf messages.
 *
 * @param <V>
 *         type of value held by this option
 * @param <T>
 *         type of field that this option is applied to
 */
public class FieldOption<V, T> implements Option<V, FieldValue<T>> {

    private final GeneratedExtension<FieldOptions, V> extension;

    /**
     * Creates an instance with the
     * <a href="https://developers.google.com/protocol-buffers/docs/proto3#custom_options">Protobuf extension</a>
     * that corresponds to this option.
     */
    protected FieldOption(GeneratedExtension<FieldOptions, V> extension) {
        this.extension = checkNotNull(extension);
    }

    /**
     * Obtains the Protobuf extension associated with the option.
     */
    protected final GeneratedExtension<FieldOptions, V> extension() {
        return extension;
    }

    @Override
    public Optional<V> valueFrom(FieldValue<T> field) {
        FieldDescriptor descriptor = field.context()
                                          .getTarget();
        FieldOptions options = descriptor.getOptions();
        boolean explicitlySet = options.hasExtension(extension);
        V value = options.getExtension(extension);
        return explicitlySet
               ? Optional.of(value)
               : Optional.empty();
    }
}
