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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.daggerquery.protobuf.autogen.BindingGraphProto;
import com.google.daggerquery.protobuf.autogen.DependencyProto;
import org.junit.Test;

import java.util.List;

public class QueryTest {

  // Tests for `DEPS` query

  @Test(expected = NullPointerException.class)
  public void testParsingQuery_WithNullQueryTypeName_ThrowsNullPointerException() {
    Query query = new Query(/*typeName = */ null, "com.google.cats.Cat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingDepsQuery_WithTwoStringParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("deps", "com.google.cats.FirstCat", "com.google.cats.SecondCat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingDepsQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("deps");
  }

  @Test(expected = NullPointerException.class)
  public void testParsingDepsQuery_WithNullParameters_ThrowsNullPointerException() {
    Query query = new Query("deps", /*parameters = */ null);
  }

  @Test
  public void testExecutingDepsQuery_WithComponentSourceNode() {
    String[] parameters = {"com.google.Component"};
    Query query = new Query("deps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    String[] expectedOutput = {"com.google.CatsFactory", "com.google.Helper"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingDepsQuery_WithCapitalizedName_WithComponentSourceNode() {
    String[] parameters = {"com.google.Component"};
    Query query = new Query("DePs", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    String[] expectedOutput = {"com.google.CatsFactory", "com.google.Helper"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingDepsQuery_WithFactorySourceNode() {
    String[] parameters = {"com.google.CatsFactory"};
    Query query = new Query("deps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    String[] expectedOutput = {"com.google.Cat"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingDepsQuery_WithLeafAsSourceNode() {
    String[] parameters = {"com.google.Cat"};
    Query query = new Query("deps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    assertEquals(0, queryExecutionResult.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingDepsQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.Kitten"};
    Query query = new Query("deps", parameters);

    query.execute(makeSimpleBindingGraph());
  }

  @Test(expected = NullPointerException.class)
  public void testExecutingDepsQuery_WithNullBindingGraph_ThrowsNullPointerException() {
    String[] parameters = {"com.google.Cat"};
    Query query = new Query("deps", parameters);

    query.execute(null);
  }

  // Tests for `ALLPATHS` query

  @Test(expected = IllegalArgumentException.class)
  public void testParsingAllPathsQuery_WithOneStringParameter_ThrowsIllegalArgumentException() {
    Query query = new Query("allpaths", "com.google.cats.Cat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingAllPathsQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("allpaths");
  }

  @Test(expected = NullPointerException.class)
  public void testParsingAllPathsQuery_WithNullParameters_ThrowsNullPointerException() {
    Query query = new Query("allpaths", /*parameters = */ null);
  }

  @Test
  public void testExecutingAllPathsQuery_WithCapitalizedName_WhenResultContainsOnePathWithTwoNodes() {
    String[] parameters = {"com.google.Cat", "com.google.Details"};
    Query query = new Query("ALLpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    String[] expectedOutput = {"com.google.Cat -> com.google.Details"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingAllPathsQuery_WhenResultContainsOnePathWithThreeNodes() {
    String[] parameters = {"com.google.CatsFactory", "com.google.Details"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    String[] expectedOutput = {"com.google.CatsFactory -> com.google.Cat -> com.google.Details"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingAllPathsQuery_WhenResultContainsMultiplePaths() {
    String[] parameters = {"com.google.Component", "com.google.Details"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    String[] expectedOutput = {
        "com.google.Component -> com.google.CatsFactory -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.Helper -> com.google.CatsFactory -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.Details"
    };
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingAllPathsQuery_WithLeafAsSourceNode() {
    String[] parameters = {"com.google.Details", "com.google.Component"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    assertEquals(0, queryExecutionResult.size());
  }

  @Test
  public void testExecutingAllPathsQuery_WhenThereIsNoPaths() {
    String[] parameters = {"com.google.CatsFactory", "com.google.Helper"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    assertEquals(0, queryExecutionResult.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingAllPathssQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.Kitten", "com.google.Details"};
    Query query = new Query("allpaths", parameters);

    query.execute(makeSimpleBindingGraph());
  }

  @Test(expected = NullPointerException.class)
  public void testExecutingAllPathsQuery_WithNullBindingGraph_ThrowsNullPointerException() {
    String[] parameters = {"com.google.Component", "com.google.Details"};
    Query query = new Query("allpaths", parameters);

    query.execute(null);
  }

  /*
   * Makes a simple acyclic binding graph with the following structure:
   *
   * com.google.Component --> com.google.CatsFactory --> com.google.Cat
   * com.google.Component --> com.google.Helper
   */
  private BindingGraphProto.BindingGraph makeSimpleBindingGraph() {
    DependencyProto.Dependency factoryNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.CatsFactory").build();
    DependencyProto.Dependency catNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Cat").build();
    DependencyProto.Dependency helperNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Helper").build();

    BindingGraphProto.BindingGraph.ListWithDependencies componentNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(factoryNode)
        .addDependency(helperNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies factoryNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(catNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies catNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies helperNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .build();

    return BindingGraphProto.BindingGraph.newBuilder()
        .putAdjacencyList("com.google.Component", componentNodeDeps)
        .putAdjacencyList("com.google.CatsFactory", factoryNodeDeps)
        .putAdjacencyList("com.google.Cat", catNodeDeps)
        .putAdjacencyList("com.google.Helper", helperNodeDeps)
        .build();
  }

  /*
   * Makes an acyclic binding graph with multiple paths between nodes with the following structure:
   *
   * com.google.Component --> com.google.CatsFactory --> com.google.Cat --> com.google.Details
   * com.google.Component --> com.google.Helper --> com.google.CatsFactory --> com.google.Cat --> com.google.Details
   * com.google.Component --> com.google.Cat --> com.google.Details
   * com.google.Component --> com.google.Details
   */
  private BindingGraphProto.BindingGraph makeBindingGraph_WithMultiplePathsBetweenTwoNodes() {
    DependencyProto.Dependency factoryNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.CatsFactory").build();
    DependencyProto.Dependency catNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Cat").build();
    DependencyProto.Dependency helperNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Helper").build();
    DependencyProto.Dependency detailsNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Details").build();

    BindingGraphProto.BindingGraph.ListWithDependencies componentNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(factoryNode)
        .addDependency(helperNode)
        .addDependency(catNode)
        .addDependency(detailsNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies factoryNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(catNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies helperNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(factoryNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies catNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(detailsNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies detailsNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .build();

    return BindingGraphProto.BindingGraph.newBuilder()
        .putAdjacencyList("com.google.Component", componentNodeDeps)
        .putAdjacencyList("com.google.CatsFactory", factoryNodeDeps)
        .putAdjacencyList("com.google.Helper", helperNodeDeps)
        .putAdjacencyList("com.google.Cat", catNodeDeps)
        .putAdjacencyList("com.google.Details", detailsNodeDeps)
        .build();
  }
}
