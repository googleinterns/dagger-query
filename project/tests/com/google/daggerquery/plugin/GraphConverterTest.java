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
import org.junit.Assert;
import org.junit.Test;
import java.util.Map;

public class GraphConverterTest {
  @Test
  public void testMakingGraph_WhenContainsOneEdge() {
    MutableNetwork<String, Integer> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ false);
    network.addEdge("A", "B", 10);

    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);

    Assert.assertTrue(adjacencyList.get("A").getDependency(0).getTarget().equals("B"));
  }

  @Test
  public void testMakingGraph_WhenContainsTwoSourceNodes() {
    MutableNetwork<String, Integer> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ false);
    network.addEdge("A", "B", 10);
    network.addEdge("B", "C", 7);
    network.addEdge("A", "C", 20);

    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);

    Assert.assertEquals(0, adjacencyList.get("C").getDependencyCount());
    Assert.assertEquals(1, adjacencyList.get("B").getDependencyCount());
    Assert.assertEquals(2, adjacencyList.get("A").getDependencyCount());
  }

  @Test
  public void testMakingGraph_WhenContainsParallelEdges() {
    MutableNetwork<String, Integer> network = makeMutableDirectedNetwork(/*allowsParallelEdges = */ true);
    network.addEdge("A", "B", 10);
    network.addEdge("A", "B", 7);

    Map<String, ListWithDependencies> adjacencyList = makeAdjacencyList(/*rootNode = */ "A", network);

    // Only the fact of the connection between two nodes is important,
    // so in case of parallel edges we just ignore them.
    Assert.assertEquals(1, adjacencyList.get("A").getDependencyCount());
    Assert.assertEquals(0, adjacencyList.get("B").getDependencyCount());
  }

  private <NodeT, EdgeT> Map<String, ListWithDependencies> makeAdjacencyList(NodeT rootNode, Network<NodeT, EdgeT> network) {
    BindingGraphProto.BindingGraph bindingGraph = new GraphConverter<NodeT, EdgeT>()
        .makeBindingGraphProto(rootNode, network);
    return bindingGraph.getAdjacencyListMap();
  }

  private <NodeT, EdgeT> MutableNetwork<NodeT, EdgeT> makeMutableDirectedNetwork(Boolean allowsParallelEdges) {
    return NetworkBuilder.directed().allowsParallelEdges(allowsParallelEdges).build();
  }
}
