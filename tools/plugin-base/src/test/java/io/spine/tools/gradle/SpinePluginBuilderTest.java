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

package io.spine.tools.gradle;

import io.spine.tools.gradle.testing.NoOp;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.file.collections.ImmutableFileCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static io.spine.tools.gradle.TaskName.annotateProto;
import static io.spine.tools.gradle.TaskName.classes;
import static io.spine.tools.gradle.TaskName.clean;
import static io.spine.tools.gradle.TaskName.compileJava;
import static io.spine.tools.gradle.TaskName.generateProto;
import static io.spine.tools.gradle.TaskName.generateTestProto;
import static io.spine.tools.gradle.TaskName.preClean;
import static io.spine.tools.gradle.TaskName.scanClassPath;
import static io.spine.tools.gradle.TaskName.verifyModel;
import static io.spine.tools.gradle.testing.GradleProject.javaPlugin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SpinePluginBuilder should")
class SpinePluginBuilderTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder()
                                .build();
        project.getPluginManager()
               .apply(javaPlugin());
    }

    @Test
    @DisplayName("create task dependant on all tasks of given name")
    void createTaskDependantOnAllTasksOfGivenName() {
        Project subProject = ProjectBuilder.builder()
                                           .withParent(project)
                                           .build();
        subProject.getPluginManager()
                  .apply(javaPlugin());
        SpinePlugin plugin = TestPlugin.INSTANCE;
        GradleTask task = plugin.newTask(annotateProto, NoOp.action())
                                .insertAfterAllTasks(compileJava)
                                .applyNowTo(subProject);
        TaskContainer subProjectTasks = subProject.getTasks();
        Task newTask = subProjectTasks.findByName(task.getName()
                                                      .value());
        assertNotNull(newTask);
        Collection<?> dependencies = newTask.getDependsOn();
        assertTrue(dependencies.contains(subProjectTasks.findByName(compileJava.value())));
        assertTrue(dependencies.contains(project.getTasks()
                                                .findByName(compileJava.value())));
    }

    @Test
    @DisplayName("create task and insert before other")
    void createTaskAndInsertBeforeOther() {
        SpinePlugin plugin = TestPlugin.INSTANCE;
        plugin.newTask(verifyModel, NoOp.action())
              .insertBeforeTask(classes)
              .applyNowTo(project);
        TaskContainer tasks = project.getTasks();
        Task classes = tasks.findByName(TaskName.classes.value());
        assertNotNull(classes);
        Task verifyModel = tasks.findByName(TaskName.verifyModel.value());
        assertTrue(classes.getDependsOn()
                          .contains(verifyModel));
    }

    @Test
    @DisplayName("create task and insert after other")
    void createTaskAndInsertAfterOther() {
        SpinePlugin plugin = TestPlugin.INSTANCE;
        plugin.newTask(verifyModel, NoOp.action())
              .insertAfterTask(compileJava)
              .applyNowTo(project);
        TaskContainer tasks = project.getTasks();
        Task compileJava = tasks.findByName(TaskName.compileJava.value());
        assertNotNull(compileJava);
        Task verifyModel = tasks.findByName(TaskName.verifyModel.value());
        assertNotNull(verifyModel);
        assertTrue(verifyModel.getDependsOn()
                              .contains(compileJava.getName()));
    }

    @Test
    @DisplayName("ignore task dependency if no such task found")
    void ignoreTaskDependencyIfNoSuchTaskFound() {
        SpinePlugin plugin = TestPlugin.INSTANCE;
        plugin.newTask(generateTestProto, NoOp.action())
              .insertAfterAllTasks(generateProto)
              .applyNowTo(project);
        TaskContainer tasks = project.getTasks();
        Task generateProto = tasks.findByName(TaskName.generateProto.value());
        assertNull(generateProto);
        Task generateTestProto = tasks.findByName(TaskName.generateTestProto.value());
        assertNotNull(generateTestProto);
    }

    @Test
    @DisplayName("not allow tasks without any connection to task graph")
    void notAllowTasksWithoutAnyConnectionToTaskGraph() {
        GradleTask.Builder builder = TestPlugin.INSTANCE.newTask(scanClassPath,
                                                                 NoOp.action());
        assertThrows(IllegalStateException.class,
                     () -> builder.applyNowTo(project));
    }

    @Test
    @DisplayName("return build task description")
    void returnBuildTaskDescription() {
        SpinePlugin plugin = TestPlugin.INSTANCE;
        GradleTask desc = plugin.newTask(preClean, NoOp.action())
                                .insertBeforeTask(clean)
                                .applyNowTo(project);
        assertEquals(preClean, desc.getName());
        assertEquals(project, desc.getProject());
    }

    @Test
    @DisplayName("create task with given inputs")
    void createTaskWithGivenInputs() throws IOException {
        SpinePlugin plugin = TestPlugin.INSTANCE;
        File input = new File(".").getAbsoluteFile();
        plugin.newTask(preClean, NoOp.action())
              .insertBeforeTask(clean)
              .withInputFiles(ImmutableFileCollection.of(input))
              .applyNowTo(project);
        Task task = project.getTasks()
                           .findByPath(preClean.value());
        assertNotNull(task);
        File singleInput = task.getInputs()
                               .getFiles()
                               .getFiles()
                               .iterator()
                               .next();
        assertEquals(input.getCanonicalFile(), singleInput.getCanonicalFile());
    }

    /**
     * A NoOp implementation of {@link SpinePlugin} used for tests.
     *
     * <p>Applying this plugin to a project causes no result.
     */
    private static class TestPlugin extends SpinePlugin {

        private static final SpinePlugin INSTANCE = new TestPlugin();

        /** Prevent direct instantiation. */
        private TestPlugin() {
        }

        @Override
        public void apply(Project project) {
            // NoOp for tests.
        }
    }
}
