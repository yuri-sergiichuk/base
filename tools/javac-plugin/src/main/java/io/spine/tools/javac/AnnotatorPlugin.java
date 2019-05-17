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

import com.google.auto.service.AutoService;
import com.google.protobuf.GeneratedMessageV3;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.util.TaskEvent.Kind.GENERATE;

@AutoService(Plugin.class)
public final class AnnotatorPlugin implements Plugin {

    @Override
    public String getName() {
        return "SpineAnnotator";
    }

    @Override
    public void init(JavacTask task, String... args) {
        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent event) {
                if (event.getKind() == GENERATE) {
                    BasicJavacTask basicTask = (BasicJavacTask) task;
                    Context context = basicTask.getContext();
                    TypeElement element = event.getTypeElement();
                    event.getCompilationUnit()
                         .getTypeDecls()
                         .forEach(tree -> annotate(tree, element, context));
                }
            }

            @Override
            public void finished(TaskEvent e) {
                // NoOp.
            }
        });
    }

    private static void annotate(Tree typeDeclaration, TypeElement element, Context context) {
        if (typeDeclaration.getKind() != CLASS) {
            return;
        }
        if (typeDeclaration instanceof JCClassDecl) {
            JCClassDecl declaration = (JCClassDecl) typeDeclaration;
            boolean message = standaloneMessage(declaration) || outerClass(element, declaration);
            if (message) {
                GeneratedSyringe.from(context)
                                .injectIntoClass(declaration);
                annotateBuilders(declaration, context);
            }
        }
    }

    private static boolean standaloneMessage(JCClassDecl declaration) {
        DeclaredType superclass = (DeclaredType) declaration.sym.getSuperclass();
        return superclass.asElement()
                         .getSimpleName()
                         .contentEquals(GeneratedMessageV3.class.getSimpleName());
    }

    private static boolean outerClass(TypeElement element, JCClassDecl declaration) {
        return element.getEnclosedElements()
                      .stream()
                      .anyMatch(enclosedType -> messageClass(declaration));
    }

    private static void annotateBuilders(JCClassDecl rootClass, Context context) {
        rootClass.accept(new JCTree.Visitor() {
            @Override
            public void visitClassDef(JCClassDecl decl) {
                annotateIfBuilder(decl, context);
            }

            @Override
            public void visitTree(JCTree tree) {
                // NoOp.
            }
        });
    }

    private static void annotateIfBuilder(JCClassDecl classDecl, Context context) {
        if (builderClass(classDecl)) {
            CanIgnoreReturnValueSyringe.from(context)
                                       .injectIntoClass(classDecl);
        }
    }

    private static boolean messageClass(JCClassDecl declaration) {
        return isSuperclass(declaration, GeneratedMessageV3.class);
    }

    private static boolean builderClass(JCClassDecl declaration) {
        return isSuperclass(declaration, GeneratedMessageV3.Builder.class);
    }

    private static boolean isSuperclass(JCClassDecl child, Class<?> parent) {
        Type supertype = child.extending.type;
        Name name = supertype.asElement()
                              .getQualifiedName();
        return name.contentEquals(parent.getCanonicalName());
    }
}
