/*
 * Copyright 2018, TeamDev. All rights reserved.
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

package io.spine.tools.protojs.field;

import com.google.common.annotations.VisibleForTesting;

import static io.spine.tools.protojs.field.Fields.capitalizedName;
import static io.spine.tools.protojs.message.MessageHandler.MESSAGE;

public final class RepeatedFieldHandler extends AbstractFieldHandler {

    private static final String LIST_ITEM = "listItem";

    private RepeatedFieldHandler(Builder builder) {
        super(builder);
    }

    @Override
    public void generateJs() {
        String jsObject = acquireJsObject();
        String value = iterateListValues(jsObject);
        setFieldValue(value);
        exitListValueIteration();
    }

    @Override
    String setterFormat() {
        String fieldName = capitalizedName(field());
        String addFunctionName = "add" + fieldName;
        String addToListFormat = MESSAGE + '.' + addFunctionName + "(%s);";
        return addToListFormat;
    }

    @VisibleForTesting
    String iterateListValues(String jsObject) {
        jsGenerator().ifNotNullOrUndefined(jsObject);
        jsGenerator().addLine(jsObject + ".forEach(");
        jsGenerator().increaseDepth();
        jsGenerator().enterBlock('(' + LIST_ITEM + ", index, array) =>");
        return LIST_ITEM;
    }

    @VisibleForTesting
    void exitListValueIteration() {
        jsGenerator().exitBlock();
        jsGenerator().decreaseDepth();
        jsGenerator().addLine(");");
        jsGenerator().exitBlock();
    }

    static Builder newBuilder() {
        return new Builder();
    }

    static class Builder extends AbstractFieldHandler.Builder<Builder> {

        @Override
        Builder self() {
            return this;
        }

        @Override
        RepeatedFieldHandler build() {
            return new RepeatedFieldHandler(this);
        }
    }
}
