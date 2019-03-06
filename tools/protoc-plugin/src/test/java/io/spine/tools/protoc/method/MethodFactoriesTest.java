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
import io.spine.tools.protoc.MethodFactoryConfiguration;
import io.spine.type.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MessageFactories should")
final class MethodFactoriesTest {

    private MethodFactories methodFactories;

    @BeforeEach
    void setUp() {
        methodFactories = new MethodFactories(MethodFactoryConfiguration.getDefaultInstance());
    }

    @DisplayName("return NoOpMessageFactory if generator name is empty")
    @Test
    void returnNoOpFactoryForEmptyFactoryName() {
        assertThat(methodFactories.newFactory(""))
                .isSameAs(MethodFactories.NoOpMethodFactory.INSTANCE);
    }

    @SuppressWarnings("NonExceptionNameEndsWithException")
    @DisplayName("throw MethodFactoryInstantiationException")
    @Nested
    final class ThrowInstantiationException {

        @DisplayName("if supplied factory name is blank")
        @Test
        void forBlankFactoryName() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> methodFactories.newFactory("   "));
        }

        @DisplayName("if implementation does not have a public constructor")
        @Test
        void withoutPublicConstructor() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> newFactoryFor(WithoutPublicConstructor.class));
        }

        @DisplayName("if implementation has private constructor")
        @Test
        void withPrivateConstructor() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> newFactoryFor(WithPrivateConstructor.class));
        }

        @DisplayName("if exception is thrown during instantiation")
        @Test
        void exceptionThrownDuringInstantiation() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> newFactoryFor(WithExceptionDuringInstantiation.class));
        }

        @DisplayName("if implementation is abstract")
        @Test
        void implementationIsAbstract() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> newFactoryFor(WithAbstractImplementation.class));
        }

        @DisplayName("if implementation is not found or not available")
        @Test
        void classIsNotFound() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> methodFactories.newFactory("com.example.NonExistingMethodFactory"));
        }

        @DisplayName("if supplied class does not implement MethodFactory")
        @Test
        void doesNotImplementMethodFactory() {
            assertThrows(MethodFactoryInstantiationException.class,
                         () -> newFactoryFor(NotMethodFactory.class));
        }
    }

    @DisplayName("return MethodFactory instance by it's fully-qualified name")
    @Test
    void returnMethodFactoryInstanceByFullyQualifiedName() {
        assertThat(newFactoryFor(StubMethodFactory.class))
                .isInstanceOf(StubMethodFactory.class);
    }

    private MethodFactory newFactoryFor(Class<?> factory) {
        return methodFactories.newFactory(factory.getName());
    }

    @Immutable
    private static class EmptyMethodFactory implements MethodFactory {

        @Override
        public ImmutableList<GeneratedMethod> newMethodsFor(MessageType messageType) {
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
}
