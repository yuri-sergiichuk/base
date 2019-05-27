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

package io.spine.js.gradle;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import io.spine.code.fs.DefaultProject;
import io.spine.code.fs.js.DefaultJsProject;
import io.spine.code.fs.js.Directory;
import io.spine.js.generate.resolve.DirectoryPattern;
import io.spine.js.generate.resolve.ExternalModule;
import io.spine.tools.gradle.GradleExtension;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static io.spine.js.gradle.ProtoJsPlugin.extensionName;
import static java.util.stream.Collectors.toList;
import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME;
import static org.gradle.api.tasks.SourceSet.TEST_SOURCE_SET_NAME;

/**
 * An extension for the {@link ProtoJsPlugin} which allows to obtain the {@code generateJsonParsers}
 * task to configure when it will be executed during the build lifecycle.
 */
@SuppressWarnings("PublicField" /* Expose fields as a Gradle extension */)
public class Extension extends GradleExtension {

    /**
     * The absolute path to the main Protobuf descriptor set file.
     */
    public String mainDescriptorSetPath;

    /**
     * The absolute path to the test Protobuf descriptor set file.
     */
    public String testDescriptorSetPath;

    /**
     * The base directory to generate code into.
     */
    public String protoBaseGenDir;

    /**
     * The subdirectory into which the JavaScript code is generated.
     */
    public String jsSubdir;

    /**
     * Names of JavaScript modules and directories they provide.
     *
     * <p>Information about modules is used to resolve imports in generated Protobuf files.
     *
     * <p>Additionally to modules specified via the property,
     * the {@linkplain #predefinedModules() predefined Spine} modules are used.
     *
     * <p>An example of the definition:
     * <pre>{@code
     * modules = [
     *      // The module provides `company/client` directory (not including subdirectories).
     *      // So, an import path like {@code ../company/client/file.js}
     *      // becomes {@code client/company/client/file.js}.
     *      'client' : ['company/client'],
     *
     *      // The module provides `company/server` directory (including subdirectories).
     *      // So, an import path like {@code ../company/server/nested/file.js}
     *      // becomes {@code server/company/server/nested/file.js}.
     *      'server' : ['company/server/*'],
     *
     *      // The module provides 'proto/company` directory.
     *      // So, an import pah like {@code ../company/file.js}
     *      // becomes {@code common-types/proto/company/file.js}.
     *      'common-types' : ['proto/company']
     * ]
     * }</pre>
     */
    public Map<String, List<String>> modules = newHashMap();
    private Task generateParsersTask;

    public static Directory mainGenProto(Project project) {
        return targetForScope(project, MAIN_SOURCE_SET_NAME);
    }

    public static Directory testGenProtoDir(Project project) {
        return targetForScope(project, TEST_SOURCE_SET_NAME);
    }

    public static Directory baseGenProtoDir(Project project) {
        Extension extension = extension(project);
        String specifiedValue = nullToEmpty(extension.protoBaseGenDir);
        Path path = pathOrDefault(specifiedValue, def(project).generated());
        return Directory.at(path);
    }

    private static Directory targetForScope(Project project, String scope) {
        Extension extension = extension(project);
        Path path = baseGenProtoDir(project).path();
        Path subdir = pathOrDefault(extension.jsSubdir, "js");
        Path targetDir = path.resolve(scope).resolve(subdir);
        return Directory.at(targetDir);
    }

    public static File mainDescriptorSet(Project project) {
        Extension extension = extension(project);
        Path path = pathOrDefault(extension.mainDescriptorSetPath,
                                  extension.defaultMainDescriptor(project));
        return path.toFile();
    }

    public static File testDescriptorSet(Project project) {
        Extension extension = extension(project);
        Path path = pathOrDefault(extension.testDescriptorSetPath,
                                  extension.defaultTestDescriptor(project));
        return path.toFile();
    }

    public static List<ExternalModule> modules(Project project) {
        Extension extension = extension(project);
        Map<String, List<String>> rawModules = extension.modules;
        List<ExternalModule> modules = newArrayList();
        for (String moduleName : rawModules.keySet()) {
            List<DirectoryPattern> patterns = patterns(rawModules.get(moduleName));
            ExternalModule module = new ExternalModule(moduleName, patterns);
            modules.add(module);
        }
        modules.addAll(predefinedModules());
        return modules;
    }

    /**
     * Returns the {@code generateJsonParsers} task configured by the {@link ProtoJsPlugin}.
     */
    @SuppressWarnings("unused") // Used in project applying the plugin.
    public Task generateParsersTask() {
        checkState(generateParsersTask != null,
                   "The 'generateJsonParsers' task was not configured by the ProtoJS plugin");
        return generateParsersTask;
    }

    /**
     * Makes the extension read-only for all plugin users.
     */
    void setGenerateParsersTask(Task generateParsersTask) {
        this.generateParsersTask = generateParsersTask;
    }

    @VisibleForTesting
    static Extension extension(Project project) {
        return (Extension)
                project.getExtensions()
                       .getByName(extensionName());
    }

    /**
     * Obtains the external modules published by Spine.
     */
    @VisibleForTesting
    static List<ExternalModule> predefinedModules() {
        return ImmutableList.of(
                ExternalModule.spineWeb(),
                ExternalModule.spineUsers()
        );
    }

    private static List<DirectoryPattern> patterns(Collection<String> rawPatterns) {
        return rawPatterns.stream()
                          .map(DirectoryPattern::of)
                          .collect(toList());
    }

    private static Path pathOrDefault(String path, Object defaultValue) {
        String pathValue = isNullOrEmpty(path)
                           ? defaultValue.toString()
                           : path;
        return Paths.get(pathValue);
    }

    private static DefaultJsProject def(Project project) {
        return DefaultJsProject.at(project.getProjectDir());
    }

    @Override
    protected DefaultProject defaultProject(Project project) {
        return def(project);
    }
}
