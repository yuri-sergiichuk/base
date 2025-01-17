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

package io.spine.reflect;

import com.google.common.base.Strings;
import com.google.errorprone.annotations.Immutable;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Verifiers if a package is a direct “parent” of the specified child.
 */
@Immutable
final class IsDirectParent implements Predicate<Package> {

    private final String childName;

    private IsDirectParent(Package child) {
        this.childName = child.getName();
    }

    static Predicate<Package> of(Package child) {
        checkNotNull(child);
        IsDirectParent result = new IsDirectParent(child);
        return result;
    }

    @Override
    public boolean test(Package candidate) {
        String commonPrefix = Strings.commonPrefix(candidate.getName(), childName);
        if (commonPrefix.isEmpty()) {
            return false;
        }
        String remainingPath = childName.substring(commonPrefix.length());
        boolean hasOnlyOneDot = remainingPath.lastIndexOf('.') == 0;
        return hasOnlyOneDot;
    }
}
