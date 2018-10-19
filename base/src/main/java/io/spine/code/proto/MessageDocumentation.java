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

package io.spine.code.proto;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * The documentation of a message in a {@code .proto} file.
 *
 * <p>Requires the following Protobuf plugin configuration:
 * <pre> {@code
 * generateProtoTasks {
 *     all().each { final task ->
 *         // If true, the descriptor set will contain line number information
 *         // and comments. Default is false.
 *         task.descriptorSetOptions.includeSourceInfo = true
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * @see <a href="https://github.com/google/protobuf-gradle-plugin/blob/master/README.md#generate-descriptor-set-files">
 *         Protobuf plugin configuration</a>
 */
public class MessageDocumentation {

    private final AbstractMessageDeclaration declaration;

    public MessageDocumentation(AbstractMessageDeclaration declaration) {
        this.declaration = declaration;
    }

    /**
     * Obtains the comments going before a rejection declaration.
     *
     * @return the comments text or {@code Optional.empty()} if there are no comments
     */
    public Optional<String> leadingComments() {
        LocationPath messagePath = getMessageLocationPath();
        return getLeadingComments(messagePath);
    }

    /**
     * Obtains the leading comments for the field.
     *
     * @param field
     *         the descriptor of the field
     * @return the field leading comments or {@code Optional.empty()} if there are no comments
     */
    public Optional<String> getFieldLeadingComments(FieldDescriptorProto field) {
        LocationPath fieldPath = getFieldLocationPath(field);
        return getLeadingComments(fieldPath);
    }

    /**
     * Obtains a leading comments by the {@link LocationPath}.
     *
     * @param locationPath
     *         the location path to get leading comments
     * @return the leading comments or empty {@code Optional} if there are no such comments
     */
    private Optional<String> getLeadingComments(LocationPath locationPath) {
        if (!declaration.getFile()
                        .hasSourceCodeInfo()) {
            String errMsg =
                    "To enable rejection generation, please configure the Gradle " +
                            "Protobuf plugin as follows: " +
                            "`task.descriptorSetOptions.includeSourceInfo = true`.";
            throw new IllegalStateException(errMsg);
        }

        DescriptorProtos.SourceCodeInfo.Location location = getLocation(locationPath);
        return location.hasLeadingComments()
               ? Optional.of(location.getLeadingComments())
               : Optional.empty();
    }

    /**
     * Returns the message location path for a top-level message definition.
     *
     * @return the message location path
     */
    private LocationPath getMessageLocationPath() {
        return new LocationPath(
                Arrays.asList(
                        FileDescriptorProto.MESSAGE_TYPE_FIELD_NUMBER,
                        getTopLevelMessageIndex())
        );
    }

    /**
     * Returns the field {@link LocationPath} for a top-level message definition.
     *
     * <p>Protobuf extensions are not supported.
     *
     * @param field
     *         the field to get location path
     * @return the field location path
     */
    private LocationPath getFieldLocationPath(FieldDescriptorProto field) {
        LocationPath locationPath = new LocationPath();

        locationPath.addAll(getMessageLocationPath());
        locationPath.add(DescriptorProtos.DescriptorProto.FIELD_FIELD_NUMBER);
        locationPath.add(getFieldIndex(field));
        return locationPath;
    }

    private int getTopLevelMessageIndex() {
        List<DescriptorProtos.DescriptorProto> messages = declaration.getFile()
                                                                     .getMessageTypeList();
        for (DescriptorProtos.DescriptorProto currentMessage : messages) {
            if (currentMessage.equals(declaration.getMessage())) {
                return messages.indexOf(declaration.getMessage());
            }
        }

        String msg = format("The rejection file \"%s\" should contain \"%s\" rejection.",
                            declaration.getFile()
                                       .getName(),
                            declaration.getMessage()
                                       .getName());
        throw new IllegalStateException(msg);
    }

    private int getFieldIndex(FieldDescriptorProto field) {
        return declaration.getMessage()
                          .getFieldList()
                          .indexOf(field);
    }

    /**
     * Returns the {@link com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location} for the
     * {@link LocationPath}.
     *
     * @param locationPath
     *         the location path
     * @return the location for the path
     */
    private DescriptorProtos.SourceCodeInfo.Location getLocation(LocationPath locationPath) {
        FileDescriptorProto declarationFile = declaration.getFile();
        for (DescriptorProtos.SourceCodeInfo.Location location : declarationFile.getSourceCodeInfo()
                                                                                .getLocationList()) {
            if (location.getPathList()
                        .equals(locationPath.getPath())) {
                return location;
            }
        }

        String msg = format("The location with %s path should be present in \"%s\".",
                            locationPath, declarationFile.getName());
        throw new IllegalStateException(msg);
    }
}