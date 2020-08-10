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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph.ListWithDependencies;
import com.google.daggerquery.protobuf.autogen.DependencyProto.Dependency;
import org.junit.Test;
import java.util.List;

public class QueryExecutorTest {
  @Test
  public void testExecutingDepsQuery_WithComponentSourceNode() {
    List<String> queryExecutionResult = new QueryExecutor().executeDepsQuery(
        "com.google.Component",
        makeSimpleBindingGraph()
    );

    String[] expectedOutput = {"com.google.CatsFactory", "com.google.Helper"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingDepsQuery_WithFactorySourceNode() {
    List<String> queryExecutionResult = new QueryExecutor().executeDepsQuery(
        "com.google.CatsFactory",
        makeSimpleBindingGraph()
    );

    String[] expectedOutput = {"com.google.Cat"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingDepsQuery_WithLeafAsSourceNode() {
    List<String> queryExecutionResult = new QueryExecutor().executeDepsQuery(
        "com.google.Cat",
        makeSimpleBindingGraph()
    );

    assertEquals(0, queryExecutionResult.size());
  }

  @Test
  public void testExecutingDepsQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
    try {
      List<String> queryExecutionResult = new QueryExecutor().executeDepsQuery(
          "com.google.Kitten",
          makeSimpleBindingGraph()
      );

      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testExecutingDepsQuery_WithNullQuery_ThrowsNullPointerException() {
    try {
      List<String> queryExecutionResult = new QueryExecutor().executeDepsQuery(
          null,
          makeSimpleBindingGraph()
      );

      fail();
    } catch (NullPointerException e) {
    }
  }

  @Test
  public void testExecutingDepsQuery_WithNullBindingGraph_ThrowsNullPointerException() {
    try {
      List<String> queryExecutionResult = new QueryExecutor().executeDepsQuery(
          "com.google.Cat",
          null
      );

      fail();
    } catch (NullPointerException e) {
    }
  }

  /*
   * Makes a simple non-cycled binding graph with the following structure:
   *
   * com.google.Component --> com.google.CatsFactory --> com.google.Cat
   * com.google.Component --> com.google.Helper
   */
  private BindingGraph makeSimpleBindingGraph() {
    Dependency factoryNode = Dependency.newBuilder().setTarget("com.google.CatsFactory").build();
    Dependency catNode = Dependency.newBuilder().setTarget("com.google.Cat").build();
    Dependency helperNode = Dependency.newBuilder().setTarget("com.google.Helper").build();

    ListWithDependencies componentNodeDeps = ListWithDependencies.newBuilder()
        .addDependency(factoryNode)
        .addDependency(helperNode)
        .build();
    ListWithDependencies factoryNodeDeps = ListWithDependencies.newBuilder()
        .addDependency(catNode)
        .build();

    return BindingGraph.newBuilder()
        .putAdjacencyList("com.google.Component", componentNodeDeps)
        .putAdjacencyList("com.google.CatsFactory", factoryNodeDeps)
        .build();
  }
}
