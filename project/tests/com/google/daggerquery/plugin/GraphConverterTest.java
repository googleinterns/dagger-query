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

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph.ListWithDependencies;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

public class GraphConverterTest {
  @Test
  public void testMakingGraph_WhenContainsOneEdge() {
    Network<String, Integer> network = makeMutableDirectedNetworkWithOneEdge("A", "B", 10);

    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);

    assertTrue(adjacencyList.get("A").getDependency(0).getTarget().equals("B"));
  }

  @Test
  public void testMakingGraph_WhenContainsTwoSourceNodes() {
    MutableNetwork<String, Integer> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ false);
    network.addEdge("A", "B", 10);
    network.addEdge("B", "C", 7);
    network.addEdge("A", "C", 20);

    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);

    assertEquals(0, adjacencyList.get("C").getDependencyCount());
    assertEquals(1, adjacencyList.get("B").getDependencyCount());
    assertEquals(2, adjacencyList.get("A").getDependencyCount());
  }

  @Test
  public void testMakingGraph_WhenContainsParallelEdges() {
    MutableNetwork<String, Integer> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ true);
    network.addEdge("A", "B", 10);
    network.addEdge("A", "B", 7);

    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);

    // Only the fact of the connection between two nodes is important,
    // so in case of parallel edges we just ignore them.
    assertEquals(1, adjacencyList.get("A").getDependencyCount());
    assertEquals(0, adjacencyList.get("B").getDependencyCount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMakingGraph_FromEmptyNetwork_ThrowsIllegalArgumentException() {
    MutableNetwork<String, Integer> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ false);
    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);
  }

  @Test(expected = NullPointerException.class)
  public void testMakingGraph_WithNullSourceNode_ThrowsNullPointerException() {
    Network<String, Integer> network = makeMutableDirectedNetworkWithOneEdge("A", "B", 10);
    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ null, network);
  }

  @Test(expected = NullPointerException.class)
  public void testMakingGraph_WithNullNetwork_ThrowsNullPointerException() {
    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMakingGraph_WithNonExistentSourceNode_ThrowsIllegalArgumentException() {
    Network<String, Integer> network = makeMutableDirectedNetworkWithOneEdge("A", "B", 10);
    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "C", network);
  }

  private <NodeT, EdgeT> Map<String, ListWithDependencies> makeAdjacencyList(NodeT rootNode, Network<NodeT, EdgeT> network) {
    BindingGraphProto.BindingGraph bindingGraph = new GraphConverter<NodeT, EdgeT>()
        .makeBindingGraphProto(rootNode, network);
    return bindingGraph.getAdjacencyListMap();
  }

  private <NodeT, EdgeT> MutableNetwork<NodeT, EdgeT> makeMutableDirectedNetwork(Boolean allowsParallelEdges) {
    return NetworkBuilder.directed().allowsParallelEdges(allowsParallelEdges).build();
  }

  private <NodeT, EdgeT> Network<NodeT, EdgeT> makeMutableDirectedNetworkWithOneEdge(NodeT source,
                                                                                     NodeT target, EdgeT edge) {
    MutableNetwork<NodeT, EdgeT> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ false);
    network.addEdge(source, target, edge);
    return network;
  }
}
