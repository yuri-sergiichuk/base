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

package io.spine.type;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.spine.Resources;
import io.spine.annotation.Internal;
import io.spine.code.proto.Type;
import io.spine.code.proto.TypeSet;

import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toSet;

/**
 * All Protobuf types known to the application.
 *
 * <p>This includes types generated by Protobuf compiler and collected into
 * the {@linkplain Resources#KNOWN_TYPES resource file}.
 *
 * @author Mikhail Mikhaylov
 * @author Alexander Litus
 * @author Alexander Yevsyukov
 * @author Dmytro Dashenkov
 */
@Internal
public class KnownTypes {

    private final TypeSet types;

    /**
     * Builds the instance by loading known types and composing lookup map for type URLs.
     */
    private KnownTypes(TypeSet types) {
        this.types = types;
    }

    /**
     * Retrieves a Java class name generated for the Protobuf type by its type url
     * to be used to parse {@link Message Message} from {@link Any}.
     *
     * @param typeUrl {@link Any} type url
     * @return Java class name
     * @throws UnknownTypeException if there is no such type known to the application
     */
    public static ClassName getClassName(TypeUrl typeUrl) throws UnknownTypeException {
        if (!instance().contains(typeUrl)) {
            throw new UnknownTypeException(typeUrl.getTypeName());
        }
        final ClassName result = instance().get(typeUrl);
        return result;
    }

    /**
     * Retrieves Protobuf type URLs known to the application.
     */
    public static Set<TypeUrl> getAllUrls() {
        return instance().types();
    }

    private Set<TypeUrl> types() {
        return types.types()
                .stream().map(Type::url)
                .collect(toSet());
    }

    private Type get(TypeName name) {
        Type result = types.find(name)
                           .orElseThrow(() -> new UnknownTypeException(name.value()));
        return result;
    }

    private ClassName get(TypeUrl typeUrl) {
        Type type = get(typeUrl.toName());
        ClassName result = type.javaClassName();
        return result;
    }

    private boolean contains(TypeUrl typeUrl) {
        TypeName name = typeUrl.toName();
        boolean result = types.contains(name);
        return result;
    }

    private Optional<TypeUrl> find(String typeName) {
        TypeName name = TypeName.of(typeName);
        Optional<TypeUrl> type = types.find(name)
                                      .map(Type::url);

        return type;
    }

    /**
     * Obtains URL for a type type by its full name.
     *
     * @return URL of the type or {@code Optional.absent()} if the type with this name is not known
     */
    static Optional<TypeUrl> tryFind(String typeName) {
        return instance().find(typeName);
    }

    /**
     * Obtains immutable set of URLs of types belonging to the passed package.
     */
    private Set<TypeUrl> fromPackage(String packageName) {
        final Set<TypeUrl> result = types().stream()
                                           .filter(TypeUrl.inPackage(packageName))
                                           .collect(toSet());
        return result;
    }

    /**
     * Retrieves all the types that belong to the given package or its subpackages.
     *
     * @param packageName proto package name
     * @return set of {@link TypeUrl TypeUrl}s of types that belong to the given package
     */
    public static Set<TypeUrl> getAllFromPackage(String packageName) {
        return instance().fromPackage(packageName);
    }

    /**
     * Obtains a Java class for the passed type URL.
     *
     * @throws UnknownTypeException if there is no Java class for the passed type URL
     */
    static <T extends Message> Class<T> getJavaClass(TypeUrl typeUrl) throws UnknownTypeException {
        checkNotNull(typeUrl);
        TypeName name = typeUrl.toName();
        Class<?> result = instance().get(name)
                                    .toJavaClass();
        // TODO:2018-06-04:dmytro.dashenkov: Check the enum case.
        return (Class<T>) result;
    }

    private static KnownTypes instance() {
        return Singleton.INSTANCE.value;
    }

    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private enum Singleton {
        INSTANCE;

        private final KnownTypes value = new KnownTypes(Loader.load());
    }
}
