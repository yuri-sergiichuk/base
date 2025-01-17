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

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors;
import io.spine.test.validation.AMessage;
import io.spine.test.validation.AnExternalConstraint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ExternalMessageConstraint should")
final class ExternalConstraintTest {

    private static final String PACKAGE = "spine.test.validation";
    private static final String VALIDATED_FIELD = PACKAGE + ".AMessage.field";

    @DisplayName("not allow null")
    @Nested
    final class NotAllowNull {

        @DisplayName("descriptor")
        @Test
        void descriptor() {
            assertThrows(NullPointerException.class, () ->
                    new ExternalMessageConstraint(null, ImmutableList.of()));
        }

        @DisplayName("target paths")
        @Test
        void paths() {
            assertThrows(NullPointerException.class, () ->
                    new ExternalMessageConstraint(AnExternalConstraint.getDescriptor(), null));
        }
    }

    @Test
    @DisplayName("build field descriptors")
    void buildFieldDescriptors() {
        Descriptors.Descriptor descriptor = AnExternalConstraint.getDescriptor();
        ExternalMessageConstraint rule =
                new ExternalMessageConstraint(descriptor, ImmutableList.of(VALIDATED_FIELD));
        assertThat(descriptor).isEqualTo(rule.getDescriptor());
        ImmutableList<Descriptors.FieldDescriptor> targets = rule.getTargets()
                                                                 .asList();
        assertThat(targets).isNotEmpty();
        Descriptors.FieldDescriptor fieldDescriptor = targets.get(0);
        assertThat(fieldDescriptor.getFullName()).isEqualTo(VALIDATED_FIELD);
    }

    @Test
    @DisplayName("build same validation rules")
    void buildSameRules() {
        String fieldName = PACKAGE + ".AMessage.field";
        Descriptors.Descriptor descriptor = AnExternalConstraint.getDescriptor();
        ExternalMessageConstraint rule =
                new ExternalMessageConstraint(descriptor, ImmutableList.of(VALIDATED_FIELD));
        assertThat(rule).isEqualTo(
                new ExternalMessageConstraint(descriptor, ImmutableList.of(VALIDATED_FIELD)));
    }

    @DisplayName("throw IllegalStateException")
    @Nested
    final class ThrowISE {

        @DisplayName("for an invalid path")
        @ParameterizedTest(name = "\"{0}\"")
        @ValueSource(strings = {"", "  ", "package-without-class"})
        void forInvalidPath(String invalidPath) {
            assertThrows(IllegalStateException.class, () ->
                    new ExternalMessageConstraint(AnExternalConstraint.getDescriptor(),
                                                  ImmutableList.of(invalidPath)));
        }

        @DisplayName("for a non-existing field path")
        @Test
        void forNonExistingField() {
            String fieldName = PACKAGE + ".AMessage.non_existing";
            Descriptors.Descriptor descriptor = AnExternalConstraint.getDescriptor();
            assertThrows(IllegalStateException.class, () ->
                    new ExternalMessageConstraint(descriptor, ImmutableList.of(fieldName)));
        }

        @DisplayName("for a non-message field path")
        @Test
        void forNonMessageField() {
            String fieldName = PACKAGE + ".AMessage.non_message_field";
            Descriptors.Descriptor descriptor = AnExternalConstraint.getDescriptor();
            assertThrows(IllegalStateException.class, () ->
                    new ExternalMessageConstraint(descriptor, ImmutableList.of(fieldName)));
        }

        @DisplayName("for a fields that are present in the validation rule but do not present in the target")
        @Test
        void forFieldsThatDoNotExistInRule() {
            Descriptors.Descriptor descriptor = AMessage.getDescriptor();
            assertThrows(IllegalStateException.class, () ->
                    new ExternalMessageConstraint(descriptor, ImmutableList.of(VALIDATED_FIELD)));
        }
    }
}
