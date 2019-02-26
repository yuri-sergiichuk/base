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

package io.spine.tools.gradle.compiler.protoc;

import io.spine.annotation.Internal;
import io.spine.code.java.ClassName;
import io.spine.tools.protoc.EnrichmentInterface;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@link GeneratedInterfaceConfig interface} configuration {@link EnrichmentMessage enrichment}
 * message selector.
 */
public final class InterfaceEnrichmentMessage
        implements EnrichmentMessage<EnrichmentInterface>, GeneratedInterfaceConfig {

    private @Nullable ClassName interfaceName;

    /** Prevents direct instantiation. **/
    InterfaceEnrichmentMessage() {
    }

    @Override
    public void markWith(@FullyQualifiedName String interfaceName) {
        checkNotNull(interfaceName);
        this.interfaceName = ClassName.of(interfaceName);
    }

    @Override
    public void ignore() {
        this.interfaceName = null;
    }

    @Internal
    @Override
    public @Nullable ClassName interfaceName() {
        return interfaceName;
    }

    @Internal
    @Override
    public EnrichmentInterface toProto() {
        return EnrichmentInterface
                .newBuilder()
                .setInterfaceName(safeName())
                .build();
    }
}
