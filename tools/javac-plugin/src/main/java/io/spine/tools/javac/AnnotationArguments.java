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

package io.spine.tools.javac;

import com.google.common.collect.ImmutableMap;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AnnotationArguments {

    @SuppressWarnings("DuplicateStringLiteralInspection") // User in other contexts.
    private static final String VALUE = "value";

    private static final AnnotationArguments EMPTY = new AnnotationArguments(ImmutableMap.of());

    private final ImmutableMap<String, Object> args;

    private AnnotationArguments(ImmutableMap<String, Object> args) {
        this.args = args;
    }

    public static AnnotationArguments value(Object constValue) {
        return new AnnotationArguments(ImmutableMap.of(VALUE, constValue));
    }

    public static AnnotationArguments empty() {
        return EMPTY;
    }

    List<JCTree.JCExpression> asExpressions(TreeMaker treeMaker, Names names) {
        checkNotNull(treeMaker);
        checkNotNull(names);

        JCTree.JCExpression[] expressions =
                args.entrySet()
                    .stream()
                    .map(argument -> assign(argument, treeMaker, names))
                    .toArray(JCTree.JCExpression[]::new);
        return List.from(expressions);
    }

    private static JCTree.JCAssign assign(Map.Entry<String, Object> argument,
                                          TreeMaker treeMaker,
                                          Names names) {
        Name name = names.fromString(argument.getKey());
        JCTree.JCIdent identifier = treeMaker.Ident(name);
        JCTree.JCLiteral literal = treeMaker.Literal(argument.getValue());
        return treeMaker.Assign(identifier, literal);
    }
}

