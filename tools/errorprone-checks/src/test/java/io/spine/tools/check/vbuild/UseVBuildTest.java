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

package io.spine.tools.check.vbuild;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.tools.check.vbuild.UseVBuild.NAME;
import static io.spine.tools.check.vbuild.UseVBuild.SUMMARY;

@DisplayName("UseVBuild check should")
class UseVBuildTest {

    private CompilationTestHelper compilationTestHelper;

    @BeforeEach
    void setUp() {
        compilationTestHelper =
                CompilationTestHelper.newInstance(UseVBuild.class, getClass());
    }

    @Test
    @DisplayName("recognize positive cases")
    void recognizePositiveCases() {
        compilationTestHelper.expectErrorMessage(NAME, msg -> msg.contains(SUMMARY))
                             .addSourceFile("given/UseVBuildPositives.java")
                             .doTest();
    }

    @Test
    @DisplayName("recognize negative cases")
    void recognizeNegativeCases() {
        compilationTestHelper.addSourceFile("given/UseVBuildNegatives.java")
                             .doTest();
    }
}
