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

package io.spine.reflect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Provides nesting information for packages {@linkplain Package#getPackages() known}
 * to the caller's {@code ClassLoader}
 *
 * @author Alexander Yevsyukov
 */
public final class PackageGraph implements Graph<PackageGraph.Node> {

    private static final Supplier<ImmutableList<Package>> packages = memoize(
            () -> {
                Package[] knownPackages = Package.getPackages();
                Arrays.sort(knownPackages, comparing(Package::getName));
                return ImmutableList.copyOf(knownPackages);
            }
    );

    private final ImmutableGraph<Node> impl;

    private PackageGraph(Graph<Node> graph) {
        this.impl = ImmutableGraph.copyOf(graph);
    }

    /**
     * Builds a graph of packages that have the common passed package prefix.
     */
    public static PackageGraph containing(String packagePrefix) {
        checkNotNullOrEmpty(packagePrefix);
        Predicate<Package> predicate = (p) -> p.getName()
                                               .startsWith(packagePrefix);
        return matching(predicate);
    }

    /**
     * Builds a graph of packages that match the passed predicate.
     */
    public static PackageGraph matching(Predicate<Package> predicate) {
        checkNotNull(predicate);
        List<Package> filteredPackages = filterPackages(predicate);
        return create(filteredPackages);
    }

    private static PackageGraph create(List<Package> filteredPackages) {
        Graph<Node> mutableGraph = buildGraph(filteredPackages);
        PackageGraph result = new PackageGraph(mutableGraph);
        return result;
    }

    private static Graph<Node> buildGraph(List<Package> packages) {
        MutableGraph<Node> graph = GraphBuilder.directed()
                                               .build();
        //TODO:2018-07-27:alexander.yevsyukov: Add nodes
        return graph;
    }

    private static List<Package> filterPackages(Predicate<Package> predicate) {
        List<Package> result =
                packages.get()
                        .stream()
                        .filter(predicate)
                        .collect(toList());
        return result;
    }

    @Override
    public Set<Node> nodes() {
        return impl.nodes();
    }

    @Override
    public Set<EndpointPair<Node>> edges() {
        return impl.edges();
    }

    @Override
    public boolean isDirected() {
        return impl.isDirected();
    }

    @Override
    public boolean allowsSelfLoops() {
        return impl.allowsSelfLoops();
    }

    @Override
    public ElementOrder<Node> nodeOrder() {
        return impl.nodeOrder();
    }

    @Override
    public Set<Node> adjacentNodes(Node node) {
        return impl.adjacentNodes(node);
    }

    @Override
    public Set<Node> predecessors(Node node) {
        return impl.predecessors(node);
    }

    @Override
    public Set<Node> successors(Node node) {
        return impl.successors(node);
    }

    @Override
    public Set<EndpointPair<Node>> incidentEdges(Node node) {
        return impl.incidentEdges(node);
    }

    @Override
    public int degree(Node node) {
        return impl.degree(node);
    }

    @Override
    public int inDegree(Node node) {
        return impl.inDegree(node);
    }

    @Override
    public int outDegree(Node node) {
        return impl.outDegree(node);
    }

    @Override
    public boolean hasEdgeConnecting(Node nodeU, Node nodeV) {
        return impl.hasEdgeConnecting(nodeU, nodeV);
    }

    /**
     * A node in the package graph.
     */
    public static final class Node {

        private final Package value;

        public Node(Package value) {
            this.value = value;
        }

        /**
         * Obtains the value stored in the node.
         */
        public Package getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Node)) {
                return false;
            }
            Node node = (Node) o;
            return Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    /**
     * Filters packages by their names.
     *
     * @implNote This class is not thread-safe.
     */
    public static final class Filter implements Predicate<Package> {

        private final Set<String> inclusions = Sets.newHashSet();
        private final Set<String> exclusions = Sets.newHashSet();

        /**
         * Adds a package prefix for being accepted by the filer.
         */
        public Filter include(String packagePrefix) {
            checkNotNullOrEmpty(packagePrefix);
            inclusions.add(packagePrefix);
            return this;
        }

        /**
         * Makes packages with the passed prefix being rejected by the filter.
         */
        public Filter exclude(String packagePrefix) {
            checkNotNullOrEmpty(packagePrefix);
            exclusions.add(packagePrefix);
            return this;
        }

        /**
         * Filters the passed package by its name.
         *
         * <p>A package is accepted if its name:
         * <ol>
         *     <li>Starts from one of the names added to {@linkplain #include(String)
         *     inclusions}.
         *     <li>Does <em>not</em> start from all the names added to {@linkplain #exclude(String)
         *     exclusions}.
         * </ol>
         */
        @Override
        public boolean test(Package aPackage) {
            String packageName = aPackage.getName();

            if (inclusions.stream()
                          .anyMatch(packageName::startsWith)) {
                return true;
            }

            if (exclusions.stream()
                          .anyMatch(packageName::startsWith)) {
                return false;
            }

            return true;
        }
    }
    private static void checkNotNullOrEmpty(String packagePrefix) {
        checkNotNull(packagePrefix);
        checkArgument(!packagePrefix.isEmpty(), "Package prefix cannot be empty");
    }
}
