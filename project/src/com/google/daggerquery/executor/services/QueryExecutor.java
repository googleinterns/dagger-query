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

package com.google.daggerquery.executor.services;

import com.google.daggerquery.executor.models.Query;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.daggerquery.protobuf.autogen.DependencyProto;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;

/**
 * Special class for executing queries by traversing a {@link BindingGraph} instance.
 *
 * <p>Responsible for executing query, which includes finding the result
 * and writing it for user in specified {@link PrintStream} instance.
 */
public class QueryExecutor {
  /**
   * Executes a {@link Query} with type {@code QueryType.DEPS}
   * by traversing {@link BindingGraph}.
   *
   * <p>The result depends on {@link com.google.daggerquery.executor.models.QueryType}.
   * For {@code QueryType.DEPS} query returns all dependencies of source node in sorted order one by one.
   */
  public List<String> executeDepsQuery(String sourceNode, BindingGraph bindingGraph) {
    if (!bindingGraph.getAdjacencyListMap().containsKey(sourceNode)) {
      // Will check if this node is a valid node in passed binding graph or not.
      DependencyProto.Dependency targetNode = DependencyProto.Dependency.newBuilder().setTarget(sourceNode).build();

      boolean containsValue;
      containsValue = bindingGraph.getAdjacencyListMap().values().stream()
          .map(list -> list.getDependencyList().contains(targetNode))
          .reduce(false, (a, b) -> a || b);

      /**
       *  This case appears when user specifies source node which is a leaf in a graph.
       *
       *  If node is a leaf, it's not presented in graph as a key,
       *  because there are no edges which starts in it.
       *
       *  However this node is a correct node in graph, so the best solution
       *  in this situation is to return an empty list with dependencies.
       */
      if (containsValue) {
        return new ArrayList<>();
      } else {
        throw new IllegalArgumentException("Specified source node doesn't exist.");
      }
    }

    return bindingGraph.getAdjacencyListMap().get(sourceNode).getDependencyList()
        .stream().map(DependencyProto.Dependency::getTarget).sorted().collect(toList());
  }
}
