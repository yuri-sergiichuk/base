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

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import io.spine.logging.Logging;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractAnnotationSyringe implements AnnotationSyringe, Logging {

    private final Name name;
    private final AnnotationArguments arguments;
    private final TreeMaker treeMaker;
    private final Names names;

    protected AbstractAnnotationSyringe(Name name, AnnotationArguments arguments, Context context) {
        this.name = checkNotNull(name);
        this.arguments = checkNotNull(arguments);
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public void injectIntoClass(JCTree.JCClassDecl classDeclaration) {
        System.err.printf("Injecting %s into %s.%n", name, classDeclaration);
        JCTree.JCModifiers modifiers = classDeclaration.mods;
        JCTree.JCIdent identifier = treeMaker.Ident(name);
        List<JCTree.JCExpression> annotationArguments = arguments.asExpressions(treeMaker, names);
        JCTree.JCAnnotation annotation = treeMaker.Annotation(identifier, annotationArguments);
        modifiers.annotations = modifiers.annotations.append(annotation);
    }
}
