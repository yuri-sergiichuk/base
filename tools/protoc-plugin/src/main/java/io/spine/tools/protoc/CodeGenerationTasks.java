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

package io.spine.tools.protoc;

import com.google.common.collect.ImmutableList;
import io.spine.type.MessageType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link CodeGenerationTask}s container.
 */
public final class CodeGenerationTasks {

    private final ImmutableList<CodeGenerationTask> tasks;

    public CodeGenerationTasks(ImmutableList<CodeGenerationTask> tasks) {
        this.tasks = checkNotNull(tasks);
    }

    /**
     * Generates code for the supplied {@code type} using all configured {@code tasks}.
     */
    public ImmutableList<CompilerOutput> generateFor(MessageType type) {
        checkNotNull(type);
        ImmutableList.Builder<CompilerOutput> result = ImmutableList.builder();
        for (CodeGenerationTask task : tasks) {
            result.addAll(task.generateFor(type));
        }
        return result.build();
    }
}
