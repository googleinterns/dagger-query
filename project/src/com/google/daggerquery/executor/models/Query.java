/*
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.google.daggerquery.executor.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.daggerquery.protobuf.autogen.DependencyProto.Dependency;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.LevenshteinDistance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static java.util.stream.Collectors.toList;

/**
 * A class which represents a query.
 *
 * <p>Each query has a String {@code name} value.
 *
 * <p>Also each query contains parameters which were passed by user.
 * The number of parameters is completely defined by the query's name.
 */
public class Query {

  private final static String DEPS_QUERY_NAME = "deps";
  private final static String ALLPATHS_QUERY_NAME = "allpaths";
  private final static String SOMEPATH_QUERY_NAME = "somepath";

  private final static int MAX_NUMBER_OF_MISPLACED_LETTERS = 3;

  /**
   * An algorithm for measuring distances between node names in a binding graph to detect typos.
   */
  private final static LevenshteinDistance levenshteinDistanceCalculator = new LevenshteinDistance();

  /**
   * The key is the name of supported query and the value is the number of parameters,
   * required by query with such name.
   */
  private final static ImmutableMap<String, Integer> supportedQueries = ImmutableMap.<String, Integer>builder()
      .put(DEPS_QUERY_NAME, 1)
      .put(ALLPATHS_QUERY_NAME, 2)
      .put(SOMEPATH_QUERY_NAME, 2)
      .build();

  private String name;
  private String[] parameters;

  /**
   * Creates an instance of {@link Query} with specified type and parameters.
   *
   * <p>Checks that passed arguments are correct and not {@code null}. The {@code parameters} argument is
   * correct when its length equals to the value in {@code supportedQueries}.
   *
   * @throws IllegalArgumentException if number of parameters is invalid or query's name is invalid
   * @throws NullPointerException if any of arguments are equal to {@code null}
   */
  public Query(String typeName, String... parameters) {
    if (typeName == null || parameters == null) {
      throw new NullPointerException("Passed arguments cannot be null.");
    } else if (!supportedQueries.keySet().contains(typeName.toLowerCase())) {
      throw new IllegalArgumentException(String.format("Query with name \"%s\" isn't supported.", typeName));
    }

    name = typeName.toLowerCase();

    if (supportedQueries.get(name) != parameters.length) {
      String exceptionMessage = String.format("The number of passed parameters is incorrect. Expected: %d, got: %d.",
          supportedQueries.get(name), parameters.length);
      throw new IllegalArgumentException(exceptionMessage);
    }

    this.parameters = parameters;
  }


  /**
   * Executes query on a {@link BindingGraph}.
   *
   * <p>For all queries return a list with strings. The content of the strings depends on a query name.
   *
   * <ul>
   * <li>For `deps` query each string represents exactly one dependency.
   * <li>For `allpaths` query each string contains a path between {@code source} and {@code target} nodes.
   * The connection between nodes is shown with the construction '->'. For example, one of the possible paths
   * may look like this: "com.google.First -> com.google.Second -> com.google.Third".
   * </ul>
   *
   * @throws MisspelledNodeNameException if the specified source node contains a typo
   * and can be corrected in no more than three steps
   *
   * @throws IllegalArgumentException if specified source node doesn't exist
   */
  public List<String> execute(BindingGraph bindingGraph) {
    switch (name) {
      case DEPS_QUERY_NAME: {
        String source = parameters[0];

        checkNodeForCorrectness(source, bindingGraph);

        return bindingGraph.getAdjacencyListMap().get(source).getDependencyList()
            .stream().map(Dependency::getTarget).sorted().collect(toList());
      }
      case ALLPATHS_QUERY_NAME: {
        String source = parameters[0];

        checkNodeForCorrectness(source, bindingGraph);

        String target = parameters[1];
        Set<String> visitedNodes = new HashSet<>();
        ImmutableList.Builder<Path> result = new ImmutableList.Builder<>();
        Path path = new Path<>();

        findAllPaths(source, target, path, bindingGraph, visitedNodes, result);
        return result.build().stream().map(Path::toString).collect(toList());
      }
      case SOMEPATH_QUERY_NAME: {
        String source = parameters[0];

        checkNodeForCorrectness(source, bindingGraph);

        String target = parameters[1];
        Set<String> visitedNodes = new HashSet<>();
        Path path = new Path<>();

        findSomePath(source, target, path, bindingGraph, visitedNodes);
        if (path.isEmpty()) {
          return new ArrayList<>();
        }

        return Arrays.asList(path.toString());
      }
    }

    throw new NotImplementedException();
  }

  /**
   * Represents a path between nodes of type {@code NodeT}.
   *
   * <p>Supports operations of adding a new node to the back and removing the last node.
   *
   * <p>Uses delimiter '->' for constructing the string representation of all data.
   */
  private static class Path<NodeT> {
    private Deque<NodeT> nodesDeque = new ArrayDeque<>();

    Path() {
    }

    Path(Path<NodeT> path) {
      nodesDeque = new ArrayDeque<>(path.nodesDeque);
    }

    void addLast(NodeT element) {
      nodesDeque.addLast(element);
    }

    NodeT removeLast() {
      return nodesDeque.removeLast();
    }

    NodeT getLast() {
      return nodesDeque.getLast();
    }

    boolean isEmpty() {
      return nodesDeque.isEmpty();
    }

    @Override
    public String toString() {
      return String.join(" -> ", nodesDeque.stream().map(NodeT::toString).collect(Collectors.toList()));
    }
  }

  /**
   * Traverses a {@link BindingGraph} starting from {@code source} node to find all paths between nodes.
   *
   * <p>Puts all processed nodes in a {@code visitedNodes} set to avoid loops. Constructs a {@code path} which is
   * an instance of {@link Path<String>}. At each recursive level this variable contains a correct path in a graph from
   * some root node which was passed in the first call and {@code target} node which is the same for all calls.
   *
   * <p>As the result fills provided {@link ImmutableList.Builder<Path>} {@code allPaths} with all possible paths between
   * root and target nodes.
   */
  private void findAllPaths(String source, String target, Path path, BindingGraph bindingGraph,
                            Set<String> visitedNodes, ImmutableList.Builder<Path> allPaths) {
    path.addLast(source);

    // If we've already found a path from `source` to `target`, we can stop and not go deeper.
    if (source.equals(target)) {
      allPaths.add(new Path<>(path));
      path.removeLast();
      return;
    }

    visitedNodes.add(source);
    for (Dependency nextNode: bindingGraph.getAdjacencyListMap().get(source).getDependencyList()) {
      if (visitedNodes.contains(nextNode.getTarget())) {
        continue;
      }

      findAllPaths(nextNode.getTarget(), target, path, bindingGraph, visitedNodes, allPaths);
    }

    visitedNodes.remove(source);
    path.removeLast();
  }

  /**
   * Traverses a {@link BindingGraph} starting from {@code source} node to find some path between nodes.
   *
   * <p>Finds the first path from {@code source} node passed in the first call and
   * {@code target} node, which is the same for all calls. Constructs this {@code path}
   * like an instance of {@link Path<String>}. When this path is found, stops going deeper.
   *
   * <p>Returns boolean value which indicates if we have already constructed a path or we have to continue execution.
   *
   * <p>Puts all processed nodes in a {@code visitedNodes} set to avoid loops.
   */
  private boolean findSomePath(String source, String target, Path path,
                               BindingGraph bindingGraph, Set<String> visitedNodes) {
    path.addLast(source);

    // Checks if we've already constructed some path from `source` to `target` and don't need to go deeper.
    if (path.getLast().equals(target)) {
      return true;
    }

    visitedNodes.add(source);
    for (Dependency nextNode: bindingGraph.getAdjacencyListMap().get(source).getDependencyList()) {
      if (visitedNodes.contains(nextNode.getTarget())) {
        continue;
      }

      if (findSomePath(nextNode.getTarget(), target, path, bindingGraph, visitedNodes)) {
        return true;
      }
    }

    visitedNodes.remove(source);
    path.removeLast();

    return false;
  }

  /**
   * Checks if the passed {@code node} is in the {@link BindingGraph} or if the user misspelled the node's name.
   *
   * <p>If the passed node is correct, does nothing.
   * Otherwise, it throws an exception, the type of which depends on the node's name.
   *
   * @throws MisspelledNodeNameException if specified source node contains a typo
   * and can be corrected in no more than {@code MAX_NUMBER_OF_MISPLACED_LETTERS} steps
   *
   * @throws IllegalArgumentException if specified source node doesn't exist
   */
  private void checkNodeForCorrectness(String node, BindingGraph bindingGraph) {
    if (bindingGraph.getAdjacencyListMap().containsKey(node)) {
      return;
    }

    // The specified node could not be found on the graph, we need to check for typos.
    List<String> closestNodes = findNodesWithClosestName(node, bindingGraph.getAdjacencyListMap().keySet());
    if (closestNodes.isEmpty()) {
      throw new IllegalArgumentException(String.format("Specified source node %s doesn't exist.", node));
    } else {
      throw new MisspelledNodeNameException(/*nodeNameWithTypo =*/ node, /*correctNodeName =*/ closestNodes);
    }
  }

  /**
   * Finds the closest nodes in a given {@link Set<String>} to the {@code originalNode}.
   *
   * <p>For measuring a distance uses <a href = "https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance metric</a>.
   * It simply calculates the number of changes required to be made to get one sequence from another,
   * where each change is a single character modification (deletion, insertion or substitution).
   *
   * <p>A node can be considered a neighbor only if the distance between it and the given {@code originalNode}
   * is less than or equal to {@code MAX_NUMBER_OF_MISPLACED_LETTERS}.
   *
   * <p>If no nodes with a similar name are found, returns an empty {@link List<String>}.
   * Otherwise, it returns a {@link List<String>} with nodes with the same distance.
   */
  private List<String> findNodesWithClosestName(String originalNode, Set<String> allNodes) {
    List<String> closestNodes = new ArrayList<>();

    for (String node: allNodes) {
      int distance = levenshteinDistanceCalculator.apply(originalNode, node);
      if (distance <= MAX_NUMBER_OF_MISPLACED_LETTERS) {
        closestNodes.add(node);
      }
    }

    return closestNodes;
  }
}
