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

import com.google.common.collect.ImmutableMap;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.daggerquery.protobuf.autogen.DependencyProto.Dependency;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  /**
   * The key is the name of supported query and the value is the number of parameters,
   * required by query with such name.
   */
  private final static ImmutableMap<String, Integer> supportedQueries = ImmutableMap.<String, Integer>builder()
      .put(DEPS_QUERY_NAME, 1)
      .put(ALLPATHS_QUERY_NAME, 2)
      .build();

  private String name;
  private String[] parameters;

  /**
   * Creates an instance of {@link Query} with specified type and parameters.
   *
   * <p>Checks that passed arguments are correct and not {@code null}. The {@code parameters} argument is
   * correct when its length equals to the value in {@code supportedQueries}.
   *
   * <p>Throws {@link IllegalArgumentException} if number of parameters is invalid or query's name is invalid.
   * Throws {@link NullPointerException} if any of arguments are equal to {@code null}.
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
   */
  public List<String> execute(BindingGraph bindingGraph) {
    switch (name) {
      case DEPS_QUERY_NAME:
        String sourceNode = parameters[0];

        if (!bindingGraph.getAdjacencyListMap().containsKey(sourceNode)) {
          throw new IllegalArgumentException("Specified source node doesn't exist.");
        }

        return bindingGraph.getAdjacencyListMap().get(sourceNode).getDependencyList()
            .stream().map(Dependency::getTarget).sorted().collect(toList());
      case ALLPATHS_QUERY_NAME:
        String source = parameters[0];

        if (!bindingGraph.getAdjacencyListMap().containsKey(source)) {
          throw new IllegalArgumentException("Specified source node doesn't exist.");
        }

        String target = parameters[1];
        Set<String> visitedNodes = new HashSet<>();
        List<List<String>> result = new ArrayList<>();
        List<String> path = new ArrayList<>();

        findAllPaths(source, target, path, bindingGraph, visitedNodes, result);
        return result.stream().map(list -> String.join(" -> ", list)).collect(toList());
    }

    throw new NotImplementedException();
  }

  /**
   * Traverses a {@link BindingGraph} starting from {@code source} node.
   *
   * <p>Puts all processed nodes in a {@code visitedNodes} set to avoid loops. Constructs a {@code path} which is
   * an instance of {@link List<String>}. At each recursive level this variable contains a correct path in a graph from
   * some root node which was passed in the first call and {@code target} node which is the same for all calls.
   *
   * <p>As the result fills provided {@link List<List<String>>} {@code allPaths} with all possible paths between
   * root and target nodes.
   */
  private void findAllPaths(String source, String target, List<String> path, BindingGraph bindingGraph,
                            Set<String> visitedNodes, List<List<String>> allPaths) {
    path.add(source);

    // If we've already found a path from `source` to `target`, we can stop and not go deeper.
    if (source.equals(target)) {
      allPaths.add(new ArrayList<>(path));
      path.remove(source);
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
    path.remove(source);
  }
}
