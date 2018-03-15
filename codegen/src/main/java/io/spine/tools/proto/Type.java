/*
 * Copyright 2018, TeamDev Ltd. All rights reserved.
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

package io.spine.tools.proto;

import com.google.protobuf.Descriptors.GenericDescriptor;
import com.google.protobuf.Message;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Protobuf type.
 *
 * @param <T> the type of the type descriptor
 * @param <P> the type of the proto message of the descriptor
 * @author Alexander Yevsyukov
 */
public abstract class Type<T extends GenericDescriptor, P extends Message> {

    private final T type;
    private final P proto;

    protected Type(T type, P proto) {
        this.type = checkNotNull(type);
        this.proto = checkNotNull(proto);
    }

    /**
     * Obtains the descriptor of the type.
     */
    public T getType() {
        return this.type;
    }

    /**
     * Obtains the proto message of the type descriptor.
     */
    public P toProto() {
        return this.proto;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, proto);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Type other = (Type) obj;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.proto, other.proto);
    }
}