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

package io.spine.tools.compiler.enrichment;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.StringValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TypeNameParser should")
class TypeRefsTest {

    private static final String PACKAGE_PREFIX = "foo.bar.";
    private static final String MESSAGE_NAME = "AMessage";

    private final TypeRefs parser = TypeRefs.enrichmentOption(PACKAGE_PREFIX);

    @Test
    @DisplayName("add package prefix to unqualified type")
    void withPrefix() {
        String parsedTypes = parser.toQualified(MESSAGE_NAME);
        assertEquals(PACKAGE_PREFIX + MESSAGE_NAME, parsedTypes);
    }

    @Test
    @DisplayName("not add package prefix to fully qualified type")
    void withoutPrefix() {
        String fqn = PACKAGE_PREFIX + MESSAGE_NAME;
        String parsedType = parser.toQualified(fqn);
        assertEquals(fqn, parsedType);
    }

    @Test
    @DisplayName("return empty collection if option is not present")
    void emptyCollection() {
        DescriptorProto definitionWithoutOption = StringValue.getDescriptor()
                                                             .toProto();
        Collection<String> result = parser.parse(definitionWithoutOption);
        assertTrue(result.isEmpty());
    }
}