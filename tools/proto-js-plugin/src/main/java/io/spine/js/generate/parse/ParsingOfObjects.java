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

package io.spine.js.generate.parse;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.code.js.Directory;
import io.spine.code.js.FileName;
import io.spine.code.proto.FileSet;
import io.spine.js.generate.FileSetEnhancement;
import io.spine.js.generate.JsFile;
import io.spine.js.generate.JsOutput;
import io.spine.js.gradle.ProtoJsPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.code.js.LibraryFile.KNOWN_TYPES;
import static io.spine.code.js.LibraryFile.KNOWN_TYPE_PARSERS;
import static io.spine.code.js.LibraryFile.SPINE_OPTIONS;
import static io.spine.code.proto.ProtoPackage.GOOGLE_PROTOBUF_PACKAGE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The JSON parsers writer used by the {@link ProtoJsPlugin}.
 *
 * <p>This class writes the JavaScript code necessary to parse messages generated by Protobuf JS
 * compiler from JSON. More specifically, the class:
 * <ol>
 *     <li>Writes all known types to the {@code known_types.js} file in generated JS code root. The
 *         types are stored in a global {@code Map} in the
 *         "{@linkplain io.spine.type.TypeUrl type-url}-to-JS-type" format.
 *     <li>Writes all standard Protobuf type parsers to the {@code known_type_parsers.js} file in
 *         generated JS code root. The parsers are stored in a global {@code Map} in the
 *         "type-url-to-parser" format.
 *     <li>Appends {@code fromJson(json)} method to all files generated from Protobuf, one for each
 *         message stored in a file.
 * </ol>
 */
public final class ParsingOfObjects extends FileSetEnhancement {

    /**
     * The path to the {@code known_type_parsers} resource which contains the parser definitions.
     */
    private static final String PARSERS_RESOURCE =
            "io/spine/tools/protojs/knowntypes/known_type_parsers";

    private ParsingOfObjects(Directory generatedRoot, FileSet fileSet) {
        super(generatedRoot, fileSet);
    }

    public static ParsingOfObjects createFor(Directory generatedRoot, FileSet protoSources) {
        checkNotNull(generatedRoot);
        checkNotNull(protoSources);
        return new ParsingOfObjects(generatedRoot, protoSources);
    }

    /**
     * Generates and writes the JS code necessary to parse proto messages from the JSON format.
     */
    @Override
    protected void processSources() {
        writeKnownTypes();
        writeKnownTypeParsers();
        writeFromJsonMethod();
    }

    /**
     * Generates global JS known types map and records it to the {@code known_types.js} file.
     *
     * <p>The types in map are stored in the
     * "{@linkplain io.spine.type.TypeUrl type-url}-to-JS-type" format.
     *
     * <p>The file is written to the root of the generated messages location.
     */
    @VisibleForTesting
    void writeKnownTypes() {
        JsOutput jsOutput = new JsOutput();
        KnownTypesGenerator generator = new KnownTypesGenerator(fileSet(), jsOutput);
        generator.generate();
        JsFile file = JsFile.createFor(generatedRoot(), KNOWN_TYPES);
        file.write(jsOutput);
    }

    /**
     * Stores the standard Protobuf type parsers code in the {@code known_type_parsers.js} file.
     *
     * <p>The parsers can be accessed via the global map, where they are stored in the
     * "{@linkplain io.spine.type.TypeUrl type-url}-to-parser" format.
     *
     * <p>The file is written to the root of the generated messages location.
     */
    @VisibleForTesting
    void writeKnownTypeParsers() {
        copyParsersCode();
        JsOutput jsOutput = new JsOutput();
        ProtoParsersGenerator generator = new ProtoParsersGenerator(jsOutput);
        generator.generate();
        JsFile file = JsFile.createFor(generatedRoot(), KNOWN_TYPE_PARSERS);
        file.append(jsOutput);
    }

    /**
     * Copies the {@code known_type_parsers.js} resource content and stores it in the target file.
     *
     * <p>Possible {@link IOException} when copying the resource is wrapped as the
     * {@link IllegalStateException}.
     */
    private void copyParsersCode() {
        try (InputStream in = ParsingOfObjects.class
                .getClassLoader()
                .getResourceAsStream(PARSERS_RESOURCE)) {
            Path path = generatedRoot().resolve(KNOWN_TYPE_PARSERS);
            Files.copy(in, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Appends the {@code fromJson(json)} methods for all known types in the corresponding files.
     *
     * <p>The standard Protobuf types and the
     * {@linkplain io.spine.option.OptionsProto Spine Options} type are skipped.
     */
    @VisibleForTesting
    void writeFromJsonMethod() {
        for (FileDescriptor file : fileSet().files()) {
            writeFromJsonMethod(file);
        }
    }

    private void writeFromJsonMethod(FileDescriptor file) {
        if (shouldSkip(file)) {
            return;
        }
        JsOutput jsOutput = new JsOutput();
        TypeParsingExtension generator = new TypeParsingExtension(file, jsOutput);
        generator.generate();
        JsFile jsFile = JsFile.createFor(generatedRoot(), file);
        jsFile.append(jsOutput);
    }

    /**
     * Checks if the writer should skip generating JSON-parsing code for messages in a file.
     */
    @VisibleForTesting
    static boolean shouldSkip(FileDescriptor file) {
        FileName fileName = FileName.from(file);
        boolean isSpineOptions = SPINE_OPTIONS.fileName()
                                              .equals(fileName);
        boolean isStandardType = file.getPackage()
                                     .startsWith(GOOGLE_PROTOBUF_PACKAGE.packageName());
        boolean shouldSkip = isSpineOptions || isStandardType;
        return shouldSkip;
    }
}
