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

package io.spine.tools.check.methodresult;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.tools.check.methodresult.HandleMethodResult.SUMMARY;

@DisplayName("HandleMethodResult check should")
class HandleMethodResultTest {

    private CompilationTestHelper compilationTestHelper;

    @BeforeEach
    void setUp() {
        compilationTestHelper =
                CompilationTestHelper.newInstance(HandleMethodResult.class, getClass());
    }

    @Test
    @DisplayName("match positive cases")
    void recognizePositiveCases() {
        compilationTestHelper.expectErrorMessage(HandleMethodResult.class.getSimpleName(),
                                                 msg -> msg.contains(SUMMARY))
                             .addSourceFile("given/HandleMethodResultPositives.java")
                             .doTest();
    }

    @Test
    @DisplayName("match negative cases")
    void recognizeNegativeCases() {
        compilationTestHelper.addSourceFile("given/HandleMethodResultNegatives.java")
                             .doTest();
    }
}
