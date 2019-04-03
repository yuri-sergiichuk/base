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
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import io.spine.annotation.SPI;

import java.util.ServiceLoader;
import java.util.Set;

/**
 * A factory of validation options for message fields.
 *
 * <p>This interface has no abstract methods. All the overridable methods are optional for
 * implementation. The default implementation retrieves empty sets.
 *
 * <p>This interface is designed as a Service Provider Interface. The implementations are
 * {@linkplain ValidatorFactoryLoader loaded} via the {@link ServiceLoader} mechanism.
 */
@SPI
public interface ValidatorFactory {

    default Set<FieldValidatingOption<?, Boolean>> optionsForBoolean() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, ByteString>>
    optionsForByteString() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, Double>> optionsForDouble() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, EnumValueDescriptor>>
    optionsForEnum() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, Float>> optionsForFloat() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, Integer>> optionsForInt() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, Long>> optionsForLong() {
        return ImmutableSet.of();
    }

    default <T extends Message> Set<FieldValidatingOption<?, T>>
    optionsForMessage() {
        return ImmutableSet.of();
    }

    default Set<FieldValidatingOption<?, String>> optionsForString() {
        return ImmutableSet.of();
    }
}
