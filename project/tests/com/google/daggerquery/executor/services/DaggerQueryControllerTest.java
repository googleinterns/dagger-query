package com.google.daggerquery.executor.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.daggerquery.protobuf.autogen.BindingGraphProto;
import com.google.daggerquery.protobuf.autogen.DependencyProto;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class DaggerQueryControllerTest {
  @Test
  public void testExecutingDepsQuery_WithValidArguments() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    new DaggerQueryController().execute(
        /*args =*/ new String[]{"Deps", "com.google.CatsFactory"},
        /*printStream =*/ new PrintStream(outputStream),
        /*sourcesLoader =*/ new TestSourcesLoader()
      );

    assertEquals("All dependencies:\n\tcom.google.Cat\n", outputStream.toString());
  }

  @Test
  public void testExecutingDepsQuery_WithInvalidQueryName() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    new DaggerQueryController().execute(
        /*args =*/ new String[]{"Ddd", "com.google.CatsFactory"},
        /*printStream =*/ new PrintStream(outputStream),
        /*sourcesLoader =*/ new TestSourcesLoader()
    );

    assertEquals("Execution failed. Reason: Query with given name isn't implemented.\n", outputStream.toString());
  }

  @Test
  public void testExecutingDepsQuery_WithMissedParameters() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    new DaggerQueryController().execute(
        /*args =*/ new String[]{"Deps"},
        /*printStream =*/ new PrintStream(outputStream),
        /*sourcesLoader =*/ new TestSourcesLoader()
    );

    assertEquals("Execution failed. Reason: The number of passed parameters is incorrect. Expected: 1, got: 0.\n",
                 outputStream.toString());
  }

  @Test
  public void testExecutingDepsQuery_WithoutParameters() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    new DaggerQueryController().execute(
        /*args =*/ new String[]{},
        /*printStream =*/ new PrintStream(outputStream),
        /*sourcesLoader =*/ new TestSourcesLoader()
    );

    assertEquals("Execution failed. You might forgot to specify the request and its parameters.\n",
                 outputStream.toString());
  }

  @Test
  public void testExecutingDepsQuery_WithNullArguments_ThrowsNullPointerException() {
    try {
      new DaggerQueryController().execute(
          /*args =*/ null,
          /*printStream =*/ new PrintStream(new ByteArrayOutputStream()),
          /*sourcesLoader =*/ new TestSourcesLoader()
      );

      fail();
    } catch (NullPointerException e) {
    }
  }

  @Test
  public void testExecutingDepsQuery_WithNullPrintStream_ThrowsNullPointerException() {
    try {
      new DaggerQueryController().execute(
          /*args =*/ new String[]{"Deps", "com.google.CatsFactory"},
          /*printStream =*/ null,
          /*sourcesLoader =*/ new TestSourcesLoader()
      );

      fail();
    } catch (NullPointerException e) {
    }
  }

  @Test
  public void testExecutingDepsQuery_WithNullSourcesLoader_ThrowsNullPointerException() {
    try {
      new DaggerQueryController().execute(
          /*args =*/ new String[]{"Deps", "com.google.CatsFactory"},
          /*printStream =*/ new PrintStream(new ByteArrayOutputStream()),
          /*sourcesLoader =*/ null
      );

      fail();
    } catch (NullPointerException e) {
    }
  }

  private class TestSourcesLoader implements SourcesLoader {
    /*
     * Makes a simple non-cycled binding graph with the following structure:
     *
     * com.google.CatsFactory --> com.google.Cat
     */
    @Override
    public BindingGraphProto.BindingGraph loadBindingGraph() throws IOException {
      DependencyProto.Dependency childNode = DependencyProto.Dependency.newBuilder().setTarget("com.google.Cat").build();

      BindingGraphProto.BindingGraph.ListWithDependencies factoryNodeDeps = BindingGraphProto.BindingGraph.ListWithDependencies.newBuilder()
          .addDependency(childNode)
          .build();

      return BindingGraphProto.BindingGraph.newBuilder()
          .putAdjacencyList("com.google.CatsFactory", factoryNodeDeps)
          .build();
    }
  }
}
