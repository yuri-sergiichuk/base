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

package io.spine.io;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Files.createParentDirs;
import static io.spine.util.Exceptions.newIllegalArgumentException;

/**
 * Additional utilities for working with files.
 *
 * <p>These utilities are specific to enable Spine working with code files in code generation,
 * project structure analysis, and other tasks that are not covered by well-known file management
 * libraries.
 *
 * <p>For more file-related utilities, please see:
 * <ul>
 *     <li>{@link java.nio.file.Files Files} from NIO
 *     <li>{@link java.nio.file.Paths Paths} from NIO2
 *     <li>{@link com.google.common.io.Files Files from Guava}
 * </ul>
 */
public final class Files2 {

    /** Prevents instantiation of this utility class. */
    private Files2() {
    }

    /**
     * Ensures that the given file exists.
     *
     * <p>Performs no action if the given file {@linkplain File#exists() already exists}.
     *
     * <p>If the given file does not exist, it is created along with its parent directories,
     * if required.
     *
     * <p>If the passed {@code File} points to the existing directory, an
     * {@link IllegalArgumentException} is thrown.
     *
     * <p>In case of any I/O issues, the respective exceptions are rethrown as
     * {@link IllegalStateException}.
     *
     * @param file
     *         a file to check
     * @return {@code true} if the file did not exist and was successfully created;
     *         {@code false} if the file already existed
     * @throws IllegalArgumentException
     *         if the given file is a directory
     * @throws IllegalStateException
     *         in case of any I/O exceptions
     */
    @CanIgnoreReturnValue
    public static boolean ensureFile(File file) {
        checkNotNull(file);
        try {
            ensureNotFolder(file);
            if (!file.exists()) {
                createParentDirs(file);
                boolean result = file.createNewFile();
                return result;
            }
            return false;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void ensureNotFolder(File file) {
        if (file.exists() && file.isDirectory()) {
            throw newIllegalArgumentException("File expected, but a folder found %s",
                                              file.getAbsolutePath());
        }
    }

    /**
     * Ensures that the file represented by the specified {@code Path exists}.
     *
     * <p>If the file already exists, no action is performed.
     *
     * <p>If the file does not exist, it is created along with its parent if required.
     *
     * <p>If the specified path represents an existing directory, an
     * {@link IllegalArgumentException} is thrown.
     *
     * <p>If any I/O errors occur, an {@link IllegalStateException} is thrown.
     *
     * @param pathToFile
     *         the path to the file to check
     * @return {@code true} if and only if the file represented by the specified path did not exist
     *         and was successfully created
     * @throws IllegalArgumentException
     *         if the given path represents a directory
     * @throws IllegalStateException
     *         if any I/O errors occur
     */
    @CanIgnoreReturnValue
    public static boolean ensureFile(Path pathToFile) {
        checkNotNull(pathToFile);
        boolean result = ensureFile(pathToFile.toFile());
        return result;
    }

    /**
     * Verifies if a passed file exists and has non-zero size.
     */
    public static boolean existsNonEmpty(File file) {
        checkNotNull(file);
        if (!file.exists()) {
            return false;
        }
        boolean nonEmpty = file.length() > 0;
        return nonEmpty;
    }
}
