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
import com.google.common.truth.StringSubject;
import com.google.common.truth.Truth;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import io.spine.code.java.ClassName;
import io.spine.tools.gradle.compiler.protoc.GeneratedInterfaces;
import io.spine.tools.gradle.compiler.protoc.GeneratedMethods;
import io.spine.tools.gradle.compiler.protoc.MessageSelectorFactory;
import io.spine.tools.protoc.given.TestInterface;
import io.spine.tools.protoc.given.UuidMethodFactory;
import io.spine.type.MessageType;
import io.spine.type.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.nio.file.Path;
import java.util.Collection;

import static io.spine.tools.protoc.given.CodeGeneratorRequestGiven.protocConfig;
import static io.spine.tools.protoc.given.CodeGeneratorRequestGiven.requestBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(TempDirectory.class)
@DisplayName("SpineProtoGenerator should")
final class SpineProtoGeneratorTest {

    private static final String TEST_PROTO_FILE = "spine/tools/protoc/test_generators.proto";

    private Path testPluginConfig;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDirPath) {
        testPluginConfig = tempDirPath.resolve("test-spine-protoc-plugin.pb");
    }

    @DisplayName("process valid CodeGeneratorRequest")
    @Test
    void processValidRequest() {
        GeneratedInterfaces interfaces = new GeneratedInterfaces();
        MessageSelectorFactory messages = interfaces.messages();
        interfaces.mark(messages.uuid(), ClassName.of(TestInterface.class));
        GeneratedMethods methods = new GeneratedMethods();
        methods.applyFactory(UuidMethodFactory.class.getName(), messages.uuid());
        CodeGeneratorRequest request = requestBuilder()
                .addProtoFile(TestGeneratorsProto.getDescriptor()
                                                 .toProto())
                .addFileToGenerate(TEST_PROTO_FILE)
                .setParameter(protocConfig(interfaces, methods, testPluginConfig))
                .build();
        MessageType type = new MessageType(EnhancedWithCodeGeneration.getDescriptor());
        File firstFile = File
                .newBuilder()
                .setName("file.proto")
                .setContent(TestInterface.class.getName() + ',')
                .setInsertionPoint(InsertionPoint.message_implements.forType(type))
                .build();
        File secondFile = File
                .newBuilder()
                .setName("file.proto")
                .setContent("public void test(){}")
                .setInsertionPoint(InsertionPoint.class_scope.forType(type))
                .build();
        TestGenerator firstGenerator = new TestGenerator(new TestCompilerOutput(firstFile),
                                                         new TestCompilerOutput(secondFile));
        CodeGeneratorResponse result = firstGenerator.process(request);
        assertEquals(2, result.getFileCount());
        assertSame(firstFile, result.getFile(0));
        assertSame(secondFile, result.getFile(1));
    }

    @DisplayName("concatenate code generated for the same insertion point")
    @Test
    void concatenateGeneratedCode() {
        GeneratedMethods methods = new GeneratedMethods();
        MessageSelectorFactory messages = methods.messages();
        methods.applyFactory(UuidMethodFactory.class.getName(), messages.uuid());
        CodeGeneratorRequest request = requestBuilder()
                .addProtoFile(TestGeneratorsProto.getDescriptor()
                                                 .toProto())
                .addFileToGenerate(TEST_PROTO_FILE)
                .setParameter(protocConfig(methods, testPluginConfig))
                .build();
        MessageType type = new MessageType(EnhancedWithCodeGeneration.getDescriptor());
        String firstMethod = "public void test1(){}";
        String secondMethod = "public void test2(){}";
        File firstFile = File
                .newBuilder()
                .setName("file.proto")
                .setContent(firstMethod)
                .setInsertionPoint(InsertionPoint.class_scope.forType(type))
                .build();
        File secondFile = firstFile
                .toBuilder()
                .setContent(secondMethod)
                .build();
        ImmutableList<CompilerOutput> compilerOutputs = ImmutableList.of(
                new TestCompilerOutput(firstFile), new TestCompilerOutput(secondFile)
        );
        TestGenerator generator = new TestGenerator(compilerOutputs);

        CodeGeneratorResponse result = generator.process(request);
        assertEquals(1, result.getFileCount());
        File file = result.getFile(0);
        StringSubject fileContent = Truth.assertThat(file.getContent());
        fileContent.contains(firstMethod);
        fileContent.contains(secondMethod);
    }

    @DisplayName("drop duplicates in generated code for the same insertion point")
    @Test
    void dropCodeDuplicates() {
        GeneratedMethods methods = new GeneratedMethods();
        MessageSelectorFactory messages = methods.messages();
        methods.applyFactory(UuidMethodFactory.class.getName(), messages.uuid());
        CodeGeneratorRequest request = requestBuilder()
                .addProtoFile(TestGeneratorsProto.getDescriptor()
                                                 .toProto())
                .addFileToGenerate(TEST_PROTO_FILE)
                .setParameter(protocConfig(methods, testPluginConfig))
                .build();
        MessageType type = new MessageType(EnhancedWithCodeGeneration.getDescriptor());
        String method = "public void test1(){}";
        File generated = File
                .newBuilder()
                .setName("file.proto")
                .setContent(method)
                .setInsertionPoint(InsertionPoint.class_scope.forType(type))
                .build();
        ImmutableList<CompilerOutput> compilerOutputs = ImmutableList.of(
                new TestCompilerOutput(generated), new TestCompilerOutput(generated)
        );
        TestGenerator generator = new TestGenerator(compilerOutputs);

        CodeGeneratorResponse result = generator.process(request);
        assertEquals(1, result.getFileCount());
        File file = result.getFile(0);
        StringSubject fileContent = Truth.assertThat(file.getContent());
        fileContent.isEqualTo(method);
    }

    @DisplayName("not process invalid CodeGeneratorRequest")
    @Test
    void notProcessInvalidRequests() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> new TestGenerator().process(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new TestGenerator().process(requestWithUnsupportedVersion()));
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> new TestGenerator().process(requestBuilder().build()));
    }

    private static CodeGeneratorRequest requestWithUnsupportedVersion() {
        CodeGeneratorRequest.Builder result = requestBuilder();
        result.setCompilerVersion(result.getCompilerVersionBuilder()
                                        .setMajor(2));
        return result.build();

    }

    private static class TestGenerator extends SpineProtoGenerator {

        private final ImmutableList<CompilerOutput> compilerOutputs;

        private TestGenerator() {
            this(ImmutableList.of());
        }

        private TestGenerator(CompilerOutput... outputs) {
            this(ImmutableList.copyOf(outputs));
        }

        private TestGenerator(ImmutableList<CompilerOutput> outputs) {
            compilerOutputs = outputs;
        }

        @Override
        protected Collection<CompilerOutput> generate(Type<?, ?> type) {
            return compilerOutputs;
        }
    }

    private static class TestCompilerOutput extends AbstractCompilerOutput {

        protected TestCompilerOutput(File file) {
            super(file);
        }
    }
}
