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

package com.google.daggerquery.executor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.daggerquery.executor.models.GraphProto;
import com.google.daggerquery.executor.models.MisspelledNodeNameException;
import com.google.daggerquery.executor.models.Query;
import com.google.daggerquery.executor.services.SourcesLoader;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A class which is responsible for parsing user's input and executing a query.
 */
public class QueryExecutor {
  /**
   * An entry point which executes a query specified as a command-line {@code args} and prints the result.
   */
  public static void main(String[] args) {
    PrintStream printStream = System.out;

    try {
      ImmutableList resultList = execute(args);
      resultList.forEach(printStream::println);
    } catch (IllegalArgumentException e) {
      printStream.println("Execution failed. Reason: " + e.getMessage());
    } catch (IOException e) {
      printStream.println("File with binding graph sources not found. Reason: " + e.getMessage());
    }
  }

  /**
   * Parses user's arguments and executes a query.
   *
   * <p>The first argument must always define the name of a query.
   * Next arguments belong to a query as its parameters.
   *
   * @throws IOException when files with binding graphs cannot be found
   * @return an instance of {@link ImmutableList} which contains query's results
   */
  public static ImmutableList<String> execute(String[] args) throws IOException {
    if (args.length == 0) {
      throw new IllegalArgumentException("You did not specify the request and its parameters.");
    }

    Query query = new Query(args[0], Arrays.copyOfRange(args, 1, args.length));
    List<BindingGraph> bindingGraphs = new SourcesLoader().loadBindingGraphs();

    ImmutableList.Builder<String> resultBuilder = new ImmutableList.Builder();

    // We assume that we successfully executed a query only if in at least one graph it was executed without fail.
    SortedSetMultimap<Integer, Exception> exceptions = TreeMultimap.create(Ordering.natural(), Ordering.allEqual());
    for (BindingGraph bindingGraph: bindingGraphs) {
      try {
        resultBuilder.addAll(query.execute(new GraphProto(bindingGraph)));
      } catch (IllegalArgumentException exception) {
        exceptions.put(3, exception);
      } catch (NoSuchElementException exception) {
        exceptions.put(2, exception);
      } catch (MisspelledNodeNameException exception) {
        exceptions.put(1, exception);
      }
    }

    ImmutableList<String> resultList = resultBuilder.build();
    if (resultList.isEmpty() && exceptions.size() > 0) {
      throw new IllegalArgumentException(exceptions.entries().iterator().next().getValue().getMessage());
    } else {
      return resultList;
    }
  }
}
