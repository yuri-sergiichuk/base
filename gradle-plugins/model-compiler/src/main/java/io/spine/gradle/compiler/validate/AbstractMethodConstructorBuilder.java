/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
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

package io.spine.gradle.compiler.validate;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.squareup.javapoet.ClassName;
import io.spine.gradle.compiler.message.MessageTypeCache;
import io.spine.gradle.compiler.message.fieldtype.FieldType;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An abstract base for the method constructor builders.
 */
abstract class AbstractMethodConstructorBuilder<T extends MethodConstructor> {

    private int fieldIndex;
    private String javaClass;
    private String javaPackage;
    private ClassName genericClassName;
    private MessageTypeCache messageTypeCache;
    private FieldDescriptorProto fieldDescriptor;
    private FieldType fieldType;

    /**
     * Builds a method constructor for the specified field.
     *
     * <p>Each successor should override that method and call
     * it via `super` as the first code line.
     *
     * @return built method constructor
     */
    @SuppressWarnings("ReturnOfNull")
    // It is OK because it does not serve as the complete method implementation.
    T build() {
        checkFields();
        return null;
    }

    AbstractMethodConstructorBuilder setFieldIndex(int fieldIndex) {
        checkArgument(fieldIndex >= 0);
        this.fieldIndex = fieldIndex;
        return this;
    }

    AbstractMethodConstructorBuilder setJavaPackage(String javaPackage) {
        checkNotNull(javaPackage);
        this.javaPackage = javaPackage;
        return this;
    }

    AbstractMethodConstructorBuilder setJavaClass(String javaClass) {
        checkNotNull(javaClass);
        this.javaClass = javaClass;
        return this;
    }

    AbstractMethodConstructorBuilder setMessageTypeCache(MessageTypeCache messageTypeCache) {
        checkNotNull(messageTypeCache);
        this.messageTypeCache = messageTypeCache;
        return this;
    }

    AbstractMethodConstructorBuilder setFieldDescriptor(FieldDescriptorProto fieldDescriptor) {
        checkNotNull(fieldDescriptor);
        this.fieldDescriptor = fieldDescriptor;
        return this;
    }

    AbstractMethodConstructorBuilder setBuilderGenericClassName(ClassName genericClassName) {
        checkNotNull(genericClassName);
        this.genericClassName = genericClassName;
        return this;
    }

    AbstractMethodConstructorBuilder setFieldType(FieldType fieldType) {
        checkNotNull(fieldType);
        this.fieldType = fieldType;
        return this;
    }

    int getFieldIndex() {
        return fieldIndex;
    }

    @Nullable
    String getJavaClass() {
        return javaClass;
    }

    @Nullable
    String getJavaPackage() {
        return javaPackage;
    }

    @Nullable
    ClassName getGenericClassName() {
        return genericClassName;
    }

    @Nullable
    MessageTypeCache getMessageTypeCache() {
        return messageTypeCache;
    }

    @Nullable
    FieldDescriptorProto getFieldDescriptor() {
        return fieldDescriptor;
    }

    @Nullable
    FieldType getFieldType() {
        return fieldType;
    }

    /**
     * Checks the builder fields.
     */
    private void checkFields() {
        checkNotNull(javaClass);
        checkNotNull(javaPackage);
        checkNotNull(messageTypeCache);
        checkNotNull(fieldDescriptor);
        checkNotNull(genericClassName);
        checkNotNull(fieldType);
        checkArgument(fieldIndex >= 0);
    }
}