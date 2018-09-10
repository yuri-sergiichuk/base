/*
 * Copyright 2018, TeamDev. All rights reserved.
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

package io.spine.tools.protojs.field;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.code.proto.FieldName;
import io.spine.tools.protojs.knowntypes.ParserMapGenerator;
import io.spine.type.TypeUrl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;

@SuppressWarnings("DuplicateStringLiteralInspection") // Duplication with unrelated modules.
public final class Fields {

    private static final String ENTRY_SUFFIX = "Entry";
    private static final String MAP_ENTRY_KEY = "key";
    private static final String MAP_ENTRY_VALUE = "value";

    private Fields() {
    }

    public static boolean isMessage(FieldDescriptor field) {
        checkNotNull(field);
        boolean isMessage = field.getType() == MESSAGE;
        return isMessage;
    }

    public static boolean isWellKnownType(FieldDescriptor field) {
        checkNotNull(field);
        if (!isMessage(field)) {
            return false;
        }
        Descriptor message = field.getMessageType();
        TypeUrl typeUrl = TypeUrl.from(message);
        boolean isWellKnownType = ParserMapGenerator.hasParser(typeUrl);
        return isWellKnownType;
    }

    public static boolean isRepeated(FieldDescriptor field) {
        checkNotNull(field);
        FieldDescriptorProto proto = field.toProto();
        boolean isRepeated = proto.getLabel() == LABEL_REPEATED && !isMap(field);
        return isRepeated;
    }

    public static boolean isMap(FieldDescriptor field) {
        checkNotNull(field);
        FieldDescriptorProto proto = field.toProto();
        if (proto.getLabel() != LABEL_REPEATED) {
            return false;
        }
        if (field.getType() != MESSAGE) {
            return false;
        }
        Descriptor fieldType = field.getMessageType();
        String mapTypeName = capitalizedName(field) + ENTRY_SUFFIX;
        boolean isMap = fieldType.getName()
                                 .equals(mapTypeName);
        return isMap;
    }

    public static FieldDescriptor keyDescriptor(FieldDescriptor field) {
        checkState(isMap(field), "Trying to get key descriptor for the non-map field.");
        FieldDescriptor descriptor = field.getMessageType()
                                          .findFieldByName(MAP_ENTRY_KEY);
        return descriptor;
    }

    public static FieldDescriptor valueDescriptor(FieldDescriptor field) {
        checkState(isMap(field), "Trying to get value descriptor for the non-map field.");
        FieldDescriptor descriptor = field.getMessageType()
                                          .findFieldByName(MAP_ENTRY_VALUE);
        return descriptor;
    }

    public static String capitalizedName(FieldDescriptor field) {
        checkNotNull(field);
        FieldDescriptorProto proto = field.toProto();
        String capitalizedName = FieldName.of(proto)
                                          .toCamelCase();
        return capitalizedName;
    }
}
