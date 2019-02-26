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

package io.spine.tools.gradle.compiler.protoc;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.protobuf.Message;
import io.spine.annotation.Internal;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.regex.qual.Regex;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates particular {@link FilePattern} selectors.
 *
 * @param <T>
 *         Protobuf configuration counterpart
 * @param <Postfix>
 *         postfix pattern selector
 * @param <Prefix>
 *         prefix pattern selector
 */
public abstract class FilePatternFactory<T extends Message,
        Postfix extends PostfixPattern<T>,
        Prefix extends PrefixPattern<T>> {

    private final Set<FilePattern<T>> patterns;

    FilePatternFactory() {
        this.patterns = Sets.newConcurrentHashSet();
    }

    /**
     * Creates a {@link PostfixPattern} selector out of a supplied {@code postfix}.
     */
    public Postfix endsWith(@Regex String postfix) {
        checkNotNull(postfix);
        Postfix result = newPostfixPattern(postfix);
        addPattern(result);
        return result;
    }
    /**
     * Creates a {@link PrefixPattern} selector out of a supplied {@code prefix}.
     */
    public Prefix startsWith(@Regex String prefix){
        checkNotNull(prefix);
        Prefix result = newPrefixPattern(prefix);
        addPattern(result);
        return result;
    }

    private void addPattern(FilePattern<T> pattern){
        if (!patterns.add(pattern)) {
            patterns.remove(pattern);
            patterns.add(pattern);
        }
    }

    /**
     * Returns currently configured file patterns.
     */
    @Internal
    ImmutableSet<FilePattern<T>> patterns() {
        return ImmutableSet.copyOf(patterns);
    }

    /**
     * Instantiates a particular {@link PostfixPattern} for the supplied {@code postfix}.
     */
    @Internal
    abstract Postfix newPostfixPattern(@NonNull @Regex String postfix);

    /**
     * Instantiates a particular {@link PrefixPattern} for the supplied {@code prefix}.
     */
    @Internal
    abstract Prefix newPrefixPattern(@NonNull @Regex String prefix);
}
