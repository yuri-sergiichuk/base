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

package io.spine.tools.protoc.iface;

import com.google.common.collect.ImmutableList;
import io.spine.code.java.ClassName;
import io.spine.tools.protoc.CodeGenerationTask;
import io.spine.tools.protoc.CompilerOutput;
import io.spine.tools.protoc.UuidImplementInterface;
import io.spine.type.MessageType;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.tools.protoc.iface.MessageImplements.implementInterface;

/**
 * Makes UUID value type implement interface supplied with the {@link UuidImplementInterface
 * configuration}.
 */
final class GenerateUuidInterface implements CodeGenerationTask {

    private final String interfaceName;

    GenerateUuidInterface(UuidImplementInterface config) {
        checkNotNull(config);
        this.interfaceName = config.getInterfaceName();
    }

    /**
     * Makes supplied {@link io.spine.base.UuidValue UuidValue} Protobuf type implement configured
     * interface.
     *
     * <p>The type does not implement an interface if interface name is empty.
     **/
    @Override
    public ImmutableList<CompilerOutput> generateFor(MessageType type) {
        if (interfaceName.isEmpty() || !type.isUuidValue()) {
            return ImmutableList.of();
        }
        return ImmutableList.of(uuidInterface(type, interfaceName));
    }

    private static CompilerOutput uuidInterface(MessageType uuidMessage, String uuidInterface) {
        ClassName interfaceName = ClassName.of(uuidInterface);
        MessageInterfaceParameters parameters =
                MessageInterfaceParameters.of(new IdentityParameter());
        MessageInterface messageInterface = new PredefinedInterface(interfaceName, parameters);
        CompilerOutput insertionPoint = implementInterface(uuidMessage, messageInterface);
        return insertionPoint;
    }
}
