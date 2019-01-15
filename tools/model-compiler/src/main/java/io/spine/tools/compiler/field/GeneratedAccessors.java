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

package io.spine.tools.compiler.field;

import com.google.common.collect.ImmutableSet;
import io.spine.annotation.Internal;
import io.spine.code.java.FieldName;
import io.spine.tools.compiler.field.type.FieldType;

import java.util.Collection;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * Property accessor methods generated by the Protobuf compiler.
 *
 * <p>Each Protobuf field results in a number of accessor methods. The count and naming of
 * the methods depends on the field type.
 */
@Internal
public final class GeneratedAccessors {

    private final FieldName propertyName;
    private final FieldType type;

    private GeneratedAccessors(FieldName propertyName, FieldType type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    /**
     * Creates an instance of {@code GeneratedAccessors} for the given field.
     *
     * @param name
     *         the name of the field associated with the accessors
     * @param type
     *         the type of the field associated with the accessors
     * @return new instance
     */
    public static GeneratedAccessors forField(io.spine.code.proto.FieldName name,
                                              FieldType type) {
        FieldName javaFieldName = FieldName.from(name);

        return new GeneratedAccessors(javaFieldName, type);
    }

    /**
     * Obtains all the names of the accessor methods.
     *
     * <p>The accessor methods may have different parameters. Some of the obtained names may
     * reference several method overloads.
     */
    public ImmutableSet<String> names() {
        ImmutableSet<String> names = names(type.generatedAccessorTemplates());
        return names;
    }

    private ImmutableSet<String> names(Collection<AccessorTemplate> templates) {
        return templates.stream()
                        .map(template -> template.format(propertyName))
                        .collect(toImmutableSet());
    }
}
