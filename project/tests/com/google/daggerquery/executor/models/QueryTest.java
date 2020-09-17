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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto;
import com.google.daggerquery.protobuf.autogen.DependencyProto;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Test;

public class QueryTest {

  @Test(expected = NullPointerException.class)
  public void testParsingQuery_WithNullQueryTypeName_ThrowsNullPointerException() {
    Query query = new Query(/*typeName = */ null, "com.google.cats.Cat");
  }

  // Tests for `DEPS` query

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

    ImmutableSet expectedOutput = ImmutableSet.of("com.google.CatsFactory", "com.google.Helper");
    assertEquals(expectedOutput, ImmutableSet.copyOf(queryExecutionResult));
  }

  @Test
  public void testExecutingDepsQuery_WithCapitalizedName_WithComponentSourceNode() {
    String[] parameters = {"com.google.Component"};
    Query query = new Query("DePs", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    ImmutableSet expectedOutput = ImmutableSet.of("com.google.CatsFactory", "com.google.Helper");
    assertEquals(expectedOutput, ImmutableSet.copyOf(queryExecutionResult));
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

  @Test
  public void testExecutingDepsQuery_WithOneTypoInNodeName_ThrowsMisspelledNodeNameException() {
    try {
      String[] parameters = {"com.google.CatsFactoryy"};
      Query query = new Query("deps", parameters);

      List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());
      fail();
    } catch (MisspelledNodeNameException e) {
      // We do not care too much about the rest of the message, but it should contain a correct node name.
      assertTrue(e.getMessage().contains("com.google.CatsFactory"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingDepsQuery_WithTooManyTyposInNodeName_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.CatsFactory...."};
    Query query = new Query("deps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());
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

    List<String> expectedOutput = Lists.newArrayList(
        "com.google.Component -> com.google.Details",
        "com.google.Component -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.CatsFactory -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.Helper -> com.google.CatsFactory -> com.google.Cat -> com.google.Details"
    );

    assertTrue(expectedOutput.size() == queryExecutionResult.size());
    assertTrue(queryExecutionResult.containsAll(expectedOutput) && expectedOutput.containsAll(queryExecutionResult));
  }

  @Test(expected = NoSuchElementException.class)
  public void testExecutingAllPathsQuery_WithLeafAsSourceNode() {
    String[] parameters = {"com.google.Details", "com.google.Component"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
  }

  @Test(expected = NoSuchElementException.class)
  public void testExecutingAllPathsQuery_WhenThereIsNoPaths() {
    String[] parameters = {"com.google.CatsFactory", "com.google.Helper"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingAllPathsQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
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

  @Test
  public void testExecutingAllPathsQuery_WithThreeTyposInNodeName_ThrowsMisspelledNodeNameException() {
    try {
      String[] parameters = {"com.google.CatsFactories", "com.google.Details"};
      Query query = new Query("allpaths", parameters);

      List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
      fail();
    } catch (MisspelledNodeNameException e) {
      // We do not care too much about the rest of the message, but it should contain a correct node name.
      assertTrue(e.getMessage().contains("com.google.CatsFactory"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingAllPathsQuery_WithTooManyTyposInNodeName_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.com.CatsFactory", "com.google.Details"};
    Query query = new Query("allpaths", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
  }

  // Tests for `SOMEPATH` query

  @Test(expected = IllegalArgumentException.class)
  public void testParsingSomePathQuery_WithOneStringParameter_ThrowsIllegalArgumentException() {
    Query query = new Query("somepath", "com.google.cats.Cat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingSomePathQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("somepath");
  }

  @Test(expected = NullPointerException.class)
  public void testParsingSomePathQuery_WithNullParameters_ThrowsNullPointerException() {
    Query query = new Query("somepath", /*parameters = */ null);
  }

  @Test
  public void testExecutingSomePathQuery_WithCapitalizedName_WhenResultContainsOnePathWithTwoNodes() {
    String[] parameters = {"com.google.Cat", "com.google.Details"};
    Query query = new Query("SoMePaTh", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    String[] expectedOutput = {"com.google.Cat -> com.google.Details"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingSomePathQuery_WhenResultContainsOnePathWithThreeNodes() {
    String[] parameters = {"com.google.CatsFactory", "com.google.Details"};
    Query query = new Query("somepath", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    String[] expectedOutput = {"com.google.CatsFactory -> com.google.Cat -> com.google.Details"};
    assertArrayEquals(expectedOutput, queryExecutionResult.toArray());
  }

  @Test
  public void testExecutingSomePathQuery_WhenResultContainsMultiplePaths() {
    String[] parameters = {"com.google.Component", "com.google.Details"};
    Query query = new Query("somepath", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    Set<String> possibleOutputs = Sets.newHashSet(
        "com.google.Component -> com.google.Details",
        "com.google.Component -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.CatsFactory -> com.google.Cat -> com.google.Details",
        "com.google.Component -> com.google.Helper -> com.google.CatsFactory -> com.google.Cat -> com.google.Details"
    );


    assertEquals(1, queryExecutionResult.size());
    assertTrue(possibleOutputs.contains(queryExecutionResult.get(0)));
  }

  @Test(expected = NoSuchElementException.class)
  public void testExecutingSomePathQuery_WithLeafAsSourceNode() {
    String[] parameters = {"com.google.Details", "com.google.Component"};
    Query query = new Query("somepath", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
  }

  @Test(expected = NoSuchElementException.class)
  public void testExecutingSomePathQuery_WhenThereIsNoPaths() {
    String[] parameters = {"com.google.CatsFactory", "com.google.Helper"};
    Query query = new Query("somepath", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingSomePathQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.Kitten", "com.google.Details"};
    Query query = new Query("somepath", parameters);

    query.execute(makeSimpleBindingGraph());
  }

  @Test(expected = NullPointerException.class)
  public void testExecutingSomePathQuery_WithNullBindingGraph_ThrowsNullPointerException() {
    String[] parameters = {"com.google.Component", "com.google.Details"};
    Query query = new Query("somepath", parameters);

    query.execute(null);
  }

  @Test
  public void testExecutingSomePathQuery_WithTwoTyposInNodeName_ThrowsMisspelledNodeNameException() {
    try {
      String[] parameters = {"c.google.CatsFactory", "com.google.Details"};
      Query query = new Query("somepath", parameters);

      List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
      fail();
    } catch (MisspelledNodeNameException e) {
      // We do not care too much about the rest of the message, but it should contain a correct node name.
      assertTrue(e.getMessage().contains("com.google.CatsFactory"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingSomePathQuery_WithTooManyTyposInNodeName_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.com.CatsFactory", "com.google.Details"};
    Query query = new Query("somepath", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());
  }

  // Tests for `RDEPS` query

  @Test(expected = IllegalArgumentException.class)
  public void testParsingRdepsQuery_WithTwoStringParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("rdeps", "com.google.cats.FirstCat", "com.google.cats.SecondCat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingRdepsQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("rdeps");
  }

  @Test(expected = NullPointerException.class)
  public void testParsingRdepsQuery_WithNullParameters_ThrowsNullPointerException() {
    Query query = new Query("rdeps", /*parameters = */ null);
  }

  @Test
  public void testExecutingRdepsQuery_WithFactorySourceNode() {
    String[] parameters = {"com.google.CatsFactory"};
    Query query = new Query("rdeps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    assertEquals(Collections.singletonList("com.google.Component"), queryExecutionResult);
  }

  @Test
  public void testExecutingRdepsQuery_WithCapitalizedName_WithFactorySourceNode() {
    String[] parameters = {"com.google.CatsFactory"};
    Query query = new Query("RDePs", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    assertEquals(Collections.singletonList("com.google.Component"), queryExecutionResult);
  }

  @Test
  public void testExecutingRdepsQuery_WithDetailsSourceNode() {
    String[] parameters = {"com.google.Details"};
    Query query = new Query("rdeps", parameters);

    List<String> queryExecutionResult = query.execute(makeBindingGraph_WithMultiplePathsBetweenTwoNodes());

    ImmutableSet expectedOutput = ImmutableSet.of("com.google.Component", "com.google.Cat");
    assertEquals(expectedOutput, ImmutableSet.copyOf(queryExecutionResult));
  }

  @Test
  public void testExecutingRdepsQuery_WithRootAsSourceNode() {
    String[] parameters = {"com.google.Component"};
    Query query = new Query("rdeps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    assertEquals(0, queryExecutionResult.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingRdepsQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.Kitten"};
    Query query = new Query("rdeps", parameters);

    query.execute(makeSimpleBindingGraph());
  }

  @Test(expected = NullPointerException.class)
  public void testExecutingRdepsQuery_WithNullBindingGraph_ThrowsNullPointerException() {
    String[] parameters = {"com.google.Cat"};
    Query query = new Query("rdeps", parameters);

    query.execute(null);
  }

  @Test
  public void testExecutingRdepsQuery_WithOneTypoInNodeName_ThrowsMisspelledNodeNameException() {
    try {
      String[] parameters = {"com.google.CatsFactoryy"};
      Query query = new Query("rdeps", parameters);

      List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());
      fail();
    } catch (MisspelledNodeNameException e) {
      // We do not care too much about the rest of the message, but it should contain a correct node name.
      assertTrue(e.getMessage().contains("com.google.CatsFactory"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingRdepsQuery_WithTooManyTyposInNodeName_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.CatsFactory...."};
    Query query = new Query("rdeps", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());
  }

  // Tests for `EXISTS` query

  @Test(expected = IllegalArgumentException.class)
  public void testParsingExistsQuery_WithTwoStringParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("exists", "com.google.cats.FirstCat", "com.google.cats.SecondCat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingExistsQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    Query query = new Query("exists");
  }

  @Test(expected = NullPointerException.class)
  public void testParsingExistsQuery_WithNullParameters_ThrowsNullPointerException() {
    Query query = new Query("exists", /*parameters = */ null);
  }

  @Test
  public void testExecutingExistsQuery_WithComponentSourceNode() {
    String[] parameters = {"com.google.Component"};
    Query query = new Query("exists", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    ImmutableSet expectedOutput = ImmutableSet.of("com.google.Component");
    assertEquals(expectedOutput, ImmutableSet.copyOf(queryExecutionResult));
  }

  @Test
  public void testExecutingExistsQuery_WithCapitalizedName_WithComponentSourceNode() {
    String[] parameters = {"com.google.Component"};
    Query query = new Query("EXIsts", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());

    ImmutableSet expectedOutput = ImmutableSet.of("com.google.Component");
    assertEquals(expectedOutput, ImmutableSet.copyOf(queryExecutionResult));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingExistsQuery_WithAbsentSourceNode_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.Kitten"};
    Query query = new Query("exists", parameters);

    query.execute(makeSimpleBindingGraph());
  }

  @Test(expected = NullPointerException.class)
  public void testExecutingExistsQuery_WithNullBindingGraph_ThrowsNullPointerException() {
    String[] parameters = {"com.google.Cat"};
    Query query = new Query("exists", parameters);

    query.execute(null);
  }

  @Test
  public void testExecutingExistsQuery_WithOneTypoInNodeName_ThrowsMisspelledNodeNameException() {
    try {
      String[] parameters = {"com.google.CatsFactoryy"};
      Query query = new Query("exists", parameters);

      List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());
      fail();
    } catch (MisspelledNodeNameException e) {
      // We do not care too much about the rest of the message, but it should contain a correct node name.
      assertTrue(e.getMessage().contains("com.google.CatsFactory"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecutingExistsQuery_WithTooManyTyposInNodeName_ThrowsIllegalArgumentException() {
    String[] parameters = {"com.google.CatsFactory...."};
    Query query = new Query("exists", parameters);

    List<String> queryExecutionResult = query.execute(makeSimpleBindingGraph());
  }

  /*
   * Makes a simple acyclic binding graph with the following structure:
   *
   * com.google.Component --> com.google.CatsFactory --> com.google.Cat
   * com.google.Component --> com.google.Helper
   */
  private Graph makeSimpleBindingGraph() {
    DependencyProto.Dependency factoryNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.CatsFactory").build();
    DependencyProto.Dependency catNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Cat").build();
    DependencyProto.Dependency helperNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Helper").build();

    BindingGraphProto.BindingGraph.ListWithDependencies componentNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(factoryNode)
        .addDependency(helperNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies catsFactoryNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .addDependency(catNode)
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies catNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .build();
    BindingGraphProto.BindingGraph.ListWithDependencies helperNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
        .build();

    BindingGraphProto.BindingGraph bindingGraph = BindingGraphProto.BindingGraph.newBuilder()
        .putAdjacencyList("com.google.Component", componentNodeDeps)
        .putAdjacencyList("com.google.CatsFactory", catsFactoryNodeDeps)
        .putAdjacencyList("com.google.Cat", catNodeDeps)
        .putAdjacencyList("com.google.Helper", helperNodeDeps)
        .build();
    return new GraphProto(bindingGraph);
  }

  /*
   * Makes an acyclic binding graph with multiple paths between nodes with the following structure:
   *
   * com.google.Component --> com.google.CatsFactory --> com.google.Cat --> com.google.Details
   * com.google.Component --> com.google.Helper --> com.google.CatsFactory --> com.google.Cat --> com.google.Details
   * com.google.Component --> com.google.Cat --> com.google.Details
   * com.google.Component --> com.google.Details
   */
  private Graph makeBindingGraph_WithMultiplePathsBetweenTwoNodes() {
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
    BindingGraphProto.BindingGraph.ListWithDependencies catsFactoryNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
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

    BindingGraphProto.BindingGraph bindingGraph = BindingGraphProto.BindingGraph.newBuilder()
        .putAdjacencyList("com.google.Component", componentNodeDeps)
        .putAdjacencyList("com.google.CatsFactory", catsFactoryNodeDeps)
        .putAdjacencyList("com.google.Helper", helperNodeDeps)
        .putAdjacencyList("com.google.Cat", catNodeDeps)
        .putAdjacencyList("com.google.Details", detailsNodeDeps)
        .build();
    return new GraphProto(bindingGraph);
  }
}
