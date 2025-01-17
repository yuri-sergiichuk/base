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

package io.spine.code.proto;

import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.logging.Logging;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * A predicate determining if a Protobuf file belongs to the specified module.
 *
 * <p>The descendants of this class are supposed to work with different
 * types of files, e.g. an original {@code .proto} file, a compiled {@code .java}, etc.
 */
public abstract class ProtoBelongsToModule implements Predicate<SourceFile>, Logging {

    @Override
    public boolean test(SourceFile file) {
        Path filePath = resolve(file);
        boolean exists = filePath.toFile()
                                 .exists();
        _debug("Checking if the file {} exists, result: {}", filePath, exists);
        return exists;
    }

    /**
     * Obtains this predicate operating with {@link FileDescriptor} instead of {@link SourceFile}.
     */
    public Predicate<FileDescriptor> forDescriptor() {
        Predicate<FileDescriptor> result = descriptor -> test(SourceFile.from(descriptor));
        return result;
    }

    /**
     * Resolves the path to the file within the module.
     *
     * @param file
     *         the file to resolve the path
     * @return the absolute path to the file
     */
    protected abstract Path resolve(SourceFile file);
}
