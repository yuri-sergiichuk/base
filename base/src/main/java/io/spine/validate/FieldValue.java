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

package io.spine.validate;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.ProtocolMessageEnum;
import io.spine.code.proto.FieldDeclaration;
import io.spine.code.proto.Option;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.validate.rule.ValidationRuleOptions.getOptionValue;
import static java.lang.String.format;

/**
 * A field value to validate.
 *
 * <p>The exact type of the value is unknown since it is set
 * by a user using a generated validating builder.
 *
 * <p>Map fields are considered in a special way and only values are validated.
 * Keys don't require validation since they are of primitive types.
 *
 * @see <a href="https://developers.google.com/protocol-buffers/docs/proto3#maps">
 *         Protobuf Maps</a>
 */
final class FieldValue<T> {

    private final T value;
    private final FieldContext context;
    private final FieldDeclaration declaration;

    private FieldValue(T value, FieldContext context, FieldDeclaration declaration) {
        this.value = value;
        this.context = context;
        this.declaration = declaration;
    }

    /**
     * Creates a new instance from the value.
     *
     * @param rawValue
     *         the value obtained via a validating builder
     * @param context
     *         the context of the field
     * @return a new instance
     */
    @SuppressWarnings("unchecked")
    static <T> FieldValue<T> of(Object rawValue, FieldContext context) {
        checkNotNull(rawValue);
        checkNotNull(context);
        T value = rawValue instanceof ProtocolMessageEnum
                       ? (T) ((ProtocolMessageEnum) rawValue).getValueDescriptor()
                       : (T) rawValue;
        FieldDescriptor fieldDescriptor = context.getTarget();
        FieldDeclaration declaration = new FieldDeclaration(fieldDescriptor);
        return new FieldValue<>(value, context, declaration);
    }

    FieldValidator<?> createValidator() {
        return createValidator(false);
    }

    FieldValidator<?> createValidatorAssumingRequired() {
        return createValidator(true);
    }

    /**
     * Creates a new validator instance according to the type of the value.
     *
     * @param assumeRequired
     *         if {@code true} validators would always assume that the field is required even
     *         if the constraint is not set explicitly
     */
    @SuppressWarnings({"OverlyComplexMethod", "unchecked"})
    private FieldValidator<?> createValidator(boolean assumeRequired) {
        JavaType fieldType = javaType();
        switch (fieldType) {
            case MESSAGE:
                return new MessageFieldValidator(castThis(), assumeRequired);
            case INT:
                return new IntegerFieldValidator(castThis());
            case LONG:
                return new LongFieldValidator(castThis());
            case FLOAT:
                return new FloatFieldValidator(castThis());
            case DOUBLE:
                return new DoubleFieldValidator(castThis());
            case STRING:
                return new StringFieldValidator(castThis(), assumeRequired);
            case BYTE_STRING:
                return new ByteStringFieldValidator(castThis());
            case BOOLEAN:
                return new BooleanFieldValidator(castThis());
            case ENUM:
                return new EnumFieldValidator(castThis());
            default:
                throw fieldTypeIsNotSupported(fieldType);
        }
    }

    /**
     * Casting this value is needed to pass it as a constructor parameter to a {@link FieldValidator}
     * subclasses, since all of them take a field value of concrete type.
     *
     * <p>Casting is safe, since {@link JavaType}, that is being checked by
     * {@link #createValidator()} maps 1 to 1 to all {@code FieldValidator} subclasses, i.e. there
     * is always going to be fitting validator.
     */
    @SuppressWarnings("unchecked")
    private <S> FieldValue<S> castThis(){
        return (FieldValue<S>) this;
    }

    private static IllegalArgumentException fieldTypeIsNotSupported(JavaType type) {
        String msg = format("The field type is not supported for validation: %s", type);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Obtains the {@link JavaType} of the value.
     *
     * <p>For a map, returns the type of the values.
     *
     * @return {@link JavaType} of {@linkplain #asList() list} elements
     */
    JavaType javaType() {
        if (!declaration.isMap()) {
            return declaration.javaType();
        }
        JavaType result = declaration.valueDeclaration()
                                     .javaType();
        return result;
    }

    /**
     * Obtains the desired option for the field.
     *
     * @param option
     *         an extension key used to obtain an option
     * @param <O>
     *         the type of the option value
     */
    <O> Option<O> option(GeneratedExtension<FieldOptions, O> option) {
        Optional<Option<O>> validationRuleOption = getOptionValue(context, option);
        if (validationRuleOption.isPresent()) {
            return validationRuleOption.get();
        }

        Option<O> result = Option.from(context.getTarget(), option);
        return result;
    }

    /**
     * Obtains the value of the option.
     *
     * @param option
     *         an extension key used to obtain an option
     * @param <O>
     *         the type of the option value
     * @return the value of the option
     */
    <O> O valueOf(GeneratedExtension<FieldOptions, O> option) {
        return option(option).value();
    }

    /**
     * Converts the value to a list.
     *
     * @return the value as a list
     */
    @SuppressWarnings({
            "unchecked", // Specific validator must call with its type.
            "ChainOfInstanceofChecks" // No other possible way to check the value type.
    })
    ImmutableList<T> asList() {
        if (value instanceof Collection) {
            Collection<T> result = (Collection<T>) value;
            return ImmutableList.copyOf(result);
        } else if (value instanceof Map) {
            Map<?, T> map = (Map<?, T>) value;
            return ImmutableList.copyOf(map.values());
        } else {
            return ImmutableList.of(value);
        }
    }

    /** Returns {@code true} if this field is default, {@code false} otherwise. */
    boolean isDefault() {
        return this.createValidator(false).fieldValueNotSet();
    }

    /** Returns the declaration of the value. */
    FieldDeclaration declaration() {
        return declaration;
    }

    /** Returns the context of the value. */
    FieldContext context() {
        return context;
    }
}
