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

import com.google.common.collect.ImmutableList;
import com.google.errorprone.VisitorState;
import com.google.errorprone.fixes.Fix;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;

import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.spine.tools.check.vbuild.UseVBuild.BUILD;

/**
 * A matcher for the {@link io.spine.tools.check.vbuild.UseVBuild} bug pattern which tracks down
 * the cases where the {@code builder.build()} statement is used.
 */
enum BuildMatcher implements ContextualMatcher<MethodInvocationTree> {

    INSTANCE;

    @SuppressWarnings("ImmutableEnumChecker")
    private static final Matcher<ExpressionTree> builderBuild =
            GeneratedValidatingBuilderWhich.callsInstanceMethod(BUILD);

    @Override
    public boolean outsideMessageContextMatches(MethodInvocationTree tree, VisitorState state) {
        return builderBuild.matches(tree, state);
    }

    @Override
    public ImmutableList<Fix> fixes(MethodInvocationTree tree) {
        ExpressionTree methodTree = tree.getMethodSelect();
        return Stream.of(BuildMethodAlternative.values())
                     .map(alt -> alt.replace(methodTree))
                     .collect(toImmutableList());
    }
}
