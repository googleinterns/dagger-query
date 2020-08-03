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

package com.google.daggerquery.plugin;

import com.google.common.graph.Network;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph.ListWithDependencies;
import com.google.daggerquery.protobuf.autogen.DependencyProto.Dependency;
import dagger.model.Binding;
import dagger.model.BindingGraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphConverter<NodeT, EdgeT> {
  /**
   * Makes a directed adjacency list based on a given {@link com.google.common.graph.Network} instance
   * and writes it to a proto model.
   *
   * <p>Uses models which were generated with <b>protocol buffers library</b> for representing edges.
   *
   * <p>Saving the directions of edges is provided by data structure itself.
   * For example, if we have an edge A --> B, we can be sure that list
   * will look like this: {@code adjacencyList["A"] = {..., "B", ...}}.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Adjacency_list">Adjacency list description</a>
   */
  BindingGraphProto.BindingGraph makeBindingGraphProto(NodeT rootNode, Network<NodeT, EdgeT> network) {
    Map<String, ListWithDependencies> adjacencyList = new HashMap<>();
    Set<String> visitedNodes = new HashSet<>();

    makeAdjacencyList(rootNode, network, adjacencyList, visitedNodes);

    BindingGraphProto.BindingGraph bindingGraph = BindingGraphProto.BindingGraph
        .newBuilder()
        .putAllAdjacencyList(adjacencyList)
        .build();

    return bindingGraph;
  }

  /**
   * Implementation of depth first search algorithm that traverses a given network.
   *
   * <p>For each node, all its child nodes are saved to {@code adjacencyList}.
   *
   * @param currentNode is a node which will be processed at this step
   * @param visitedNodes is a set that contains all processed nodes to avoid loops
   */
  private void makeAdjacencyList(NodeT currentNode, Network<NodeT, EdgeT> network,
                                 Map<String, ListWithDependencies> adjacencyList, Set<String> visitedNodes) {
    String nodeKey = makeStringFromNode(currentNode);
    visitedNodes.add(nodeKey);

    ListWithDependencies.Builder listWithDependenciesBuilder = ListWithDependencies.newBuilder();
    for (NodeT childNode: network.successors(currentNode)) {
      String childNodeKey = makeStringFromNode(childNode);
      listWithDependenciesBuilder.addDependency(Dependency.newBuilder().setTarget(childNodeKey).build());

      if (!visitedNodes.contains(childNodeKey)) {
        makeAdjacencyList(childNode, network, adjacencyList, visitedNodes);
      }
    }

    adjacencyList.put(nodeKey, listWithDependenciesBuilder.build());
  }

  /**
   * Makes a string representation for a given node based on its type.
   *
   * <p>For <b>{@link dagger.model.Binding}</b> it's a string representation of <b>{@link dagger.model.Key}</b> field.
   * For <b>{@link dagger.model.BindingGraph.ComponentNode}</b> nodes it's a full name of a component.
   * For other nodes returns their string representation constructed with {@code toString()} method.
   */
  private String makeStringFromNode(NodeT node) {
    if (node instanceof Binding) {
      return ((Binding) node).key().toString();
    } else if (node instanceof BindingGraph.ComponentNode) {
      return ((BindingGraph.ComponentNode) node).componentPath().currentComponent().toString();
    }

    return node.toString();
  }
}
