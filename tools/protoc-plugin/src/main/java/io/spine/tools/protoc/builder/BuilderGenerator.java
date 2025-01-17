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

package io.spine.tools.protoc.builder;

import com.google.common.collect.ImmutableSet;
import io.spine.tools.protoc.CompilerOutput;
import io.spine.tools.protoc.SpineProtoGenerator;
import io.spine.type.MessageType;
import io.spine.type.Type;

import java.util.Collection;

import static io.spine.tools.protoc.builder.BuilderImplements.implementValidatingBuilder;

/**
 * A code generator which makes the generated message builders implement
 * {@link io.spine.protobuf.ValidatingBuilder}.
 */
public final class BuilderGenerator extends SpineProtoGenerator {

    /**
     * Prevents direct instantiation.
     */
    private BuilderGenerator() {
        super();
    }

    /**
     * Creates a new instance of the generator.
     */
    public static BuilderGenerator instance() {
        return new BuilderGenerator();
    }

    @Override
    protected Collection<CompilerOutput> generate(Type<?, ?> type) {
        if (type instanceof MessageType) {
            CompilerOutput insertionPoint = implementValidatingBuilder((MessageType) type);
            return ImmutableSet.of(insertionPoint);
        } else {
            return ImmutableSet.of();
        }
    }
}
