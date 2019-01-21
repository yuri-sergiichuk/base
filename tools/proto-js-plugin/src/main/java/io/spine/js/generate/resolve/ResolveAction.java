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

import io.spine.code.js.ImportPath;

/**
 * An action performed to resolve particular types of imports.
 */
abstract class ResolveAction {

    /**
     * Attempts to resolve the import.
     *
     * @param resolvable
     *         the import to resolve
     * @return the resolved import or the same import if it was not resolved
     */
    final ImportSnippet attemptResolve(ImportSnippet resolvable) {
        ImportPath path = resolvable.path();
        if (!isApplicableTo(path) || shouldSkip(path)) {
            return resolvable;
        }
        return resolve(resolvable);
    }

    /**
     * Resolves an import and replaces the import path.
     */
    abstract ImportSnippet resolve(ImportSnippet resolvable);

    /**
     * Tells whether the import is supported by the class.
     */
    abstract boolean isApplicableTo(ImportPath importPath);

    /**
     * Tells whether the import resolving should be skipped for the currently processed module.
     */
    abstract boolean shouldSkip(ImportPath importPath);
}
