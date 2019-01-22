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

package io.spine.js.generate.resolve;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import io.spine.code.js.Directory;
import io.spine.code.js.FileName;
import io.spine.code.js.ImportPath;
import io.spine.js.generate.resolve.given.Given;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ResolveRelativeImport action should")
class ResolveRelativeImportTest {

    private final Directory generatedRoot = Directory.at(Paths.get("src/test/resources"));
    private final ResolveRelativeImport action = new ResolveRelativeImport(generatedRoot,
                                                                           ImmutableList.of());

    @Test
    @DisplayName("be applicable only to relative imports of generated Protobufs")
    void beApplicableToRelativeProtoImports() {
        ImportPath importPath = ImportPath.of("../options_pb.js");
        boolean applicable = action.isApplicableTo(importPath);
        assertTrue(applicable);
    }

    @Test
    @DisplayName("return the same import snippet if was not resolved")
    void handleUnresolved() {
        FileName importSource = FileName.from(Any.getDescriptor()
                                                 .getFile());
        ImportSnippet resolvable = Given.importWithPath("./unknown/options_pb.js", importSource);
        ImportSnippet resolved = action.resolve(resolvable);
        assertEquals(resolvable, resolved);
    }

    @Test
    @DisplayName("skip imports of files exposed by current module")
    void skipImports() {
        ImportPath importPath = ImportPath.of("../fake_pb.js");
        boolean shouldSkip = action.shouldNotResolve(importPath);
        assertTrue(shouldSkip);
    }
}
