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

package io.spine.tools.protoc.method;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import io.spine.code.proto.MessageType;
import io.spine.protoc.MethodBody;
import io.spine.protoc.MethodFactory;
import io.spine.tools.protoc.GeneratedMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("MessageFactories should")
final class MethodFactoriesTest {

    @DisplayName("return NoOpMessageFactory")
    @Nested
    final class ReturnNoOpFactory {

        @DisplayName("if generator name is")
        @ParameterizedTest(name = "\"{0}\"")
        @ValueSource(strings = {"", "  "})
        void forBlankGeneratorName(String generatorName) {
            GeneratedMethod spec = specForGenerator(generatorName);

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }

        @DisplayName("if implementation does not have a public constructor")
        @Test
        void withoutPublicConstructor() {
            GeneratedMethod spec = specForGenerator(WithoutPublicConstructor.class);

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }

        @DisplayName("if implementation has private constructor")
        @Test
        void withPrivateConstructor() {
            GeneratedMethod spec = specForGenerator(WithPrivateConstructor.class);

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }

        @DisplayName("if exception is thrown during instantiation")
        @Test
        void exceptionThrownDuringInstantiation() {
            GeneratedMethod spec = specForGenerator(WithExceptionDuringInstantiation.class);

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }

        @DisplayName("if implementation is abstract")
        @Test
        void implementationIsAbstract() {
            GeneratedMethod spec = specForGenerator(WithAbstractImplementation.class);

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }

        @DisplayName("if implementation is not found or not available")
        @Test
        void classIsNotFound() {
            GeneratedMethod spec = specForGenerator("com.example.NonExistingMethodFactory");

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }

        @DisplayName("if supplied class does not implement MethodFactory")
        @Test
        void doesNotImplementMethodFactory() {
            GeneratedMethod spec = specForGenerator(NotMethodFactory.class);

            assertThat(MethodFactories.newFactoryFor(spec))
                    .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
        }
    }

    @DisplayName("return MethodFactory instance by it's fully-qualified name")
    @Test
    void returnMethodFactoryInstanceByFullyQualifiedName() {
        GeneratedMethod spec = specForGenerator(StubMethodFactory.class);

        assertThat(MethodFactories.newFactoryFor(spec))
                .isInstanceOf(StubMethodFactory.class);
    }

    @Immutable
    private static class EmptyMethodFactory implements MethodFactory {

        @Override
        public ImmutableList<MethodBody> newMethodsFor(MessageType messageType) {
            return ImmutableList.of();
        }
    }

    @Immutable
    public static class StubMethodFactory extends EmptyMethodFactory {

        public StubMethodFactory() {
        }
    }

    @Immutable
    @SuppressWarnings("EmptyClass") // for test reasons
    private static class WithoutPublicConstructor extends EmptyMethodFactory {

    }

    @Immutable
    public static class WithPrivateConstructor extends EmptyMethodFactory {

        private WithPrivateConstructor() {
        }
    }

    @Immutable
    public static class WithExceptionDuringInstantiation extends EmptyMethodFactory {

        public WithExceptionDuringInstantiation() {
            throw new RuntimeException("Test exception during instantiation");
        }
    }

    @Immutable
    // for test reasons
    @SuppressWarnings({"AbstractClassNeverImplemented", "ConstructorNotProtectedInAbstractClass"})
    public abstract static class WithAbstractImplementation extends EmptyMethodFactory {

        public WithAbstractImplementation() {
        }
    }

    public static class NotMethodFactory {

        public NotMethodFactory() {
        }
    }

    private static GeneratedMethod specForGenerator(Class<?> generator) {
        return specForGenerator(generator.getName());
    }

    private static GeneratedMethod specForGenerator(String generatorName) {
        return GeneratedMethod.newBuilder()
                              .setGeneratorName(generatorName)
                              .build();
    }
}
