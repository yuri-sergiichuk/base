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

package io.spine.code.proto;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.spine.code.proto.FileName.of;
import static io.spine.testing.DisplayNames.NOT_ACCEPT_NULLS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FileName should")
class FileNameTest {

    @Test
    @DisplayName(NOT_ACCEPT_NULLS)
    void pass_null_tolerance_check() {
        new NullPointerTester().testStaticMethods(FileName.class,
                                                  NullPointerTester.Visibility.PACKAGE);
    }

    @Test
    @DisplayName("require standard extension")
    void require_standard_extension() {
        assertThrows(IllegalArgumentException.class,
                     () -> of("some_thing"));
    }

    @Test
    @DisplayName("return words")
    void return_words() {
        List<String> words = of("some_file_name.proto").words();

        assertEquals(ImmutableList.of("some", "file", "name"), words);
    }

    @Test
    @DisplayName("calculate outer class name")
    @SuppressWarnings("DuplicateStringLiteralInspection")
    void calculate_outer_class_name() {
        assertEquals("Rejections", of("rejections.proto").nameOnlyCamelCase());
        assertEquals("ManyRejections", of("many_rejections.proto").nameOnlyCamelCase());
        assertEquals("ManyMoreRejections", of("many_more_rejections.proto").nameOnlyCamelCase());
    }

    @Test
    @DisplayName("return file name without extension")
    void return_file_name_without_extension() {
        assertEquals("package/commands", of("package/commands.proto").nameWithoutExtension());
    }

    @Test
    @DisplayName("tell commands file kind")
    void tell_commands_file_kind() {
        FileName commandsFile = of("my_commands.proto");

        assertTrue(commandsFile.isCommands());
        assertFalse(commandsFile.isEvents());
        assertFalse(commandsFile.isRejections());
    }

    @Test
    @DisplayName("tell events file kind")
    void tell_events_file_kind() {
        FileName eventsFile = of("project_events.proto");

        assertTrue(eventsFile.isEvents());
        assertFalse(eventsFile.isCommands());
        assertFalse(eventsFile.isRejections());
    }

    @Test
    @DisplayName("tell rejection file kind")
    void tell_rejections_file_kind() {
        FileName rejectionsFile = of("rejections.proto");

        assertTrue(rejectionsFile.isRejections());
        assertFalse(rejectionsFile.isCommands());
        assertFalse(rejectionsFile.isEvents());
    }
}