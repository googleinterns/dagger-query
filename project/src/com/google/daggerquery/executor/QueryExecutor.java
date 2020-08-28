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
import com.google.daggerquery.executor.models.MisspelledNodeNameException;
import com.google.daggerquery.executor.models.Query;
import com.google.daggerquery.executor.services.SourcesLoader;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * A class which is responsible for parsing user's input and executing a query.
 */
public class QueryExecutor {
  /**
   * Parses user's arguments and executes a query.
   *
   * <p>The first argument must always define the name of a query.
   * Next arguments belong to a query as its parameters.
   */
  public static void main(String[] args) {
    PrintStream printStream = System.out;

    try {
      if (args.length == 0) {
        throw new IllegalArgumentException("You did not specify the request and its parameters.");
      }

      Query query = new Query(args[0], Arrays.copyOfRange(args, 1, args.length));
      List<BindingGraph> bindingGraphs = new SourcesLoader().loadBindingGraphs();

      ImmutableList.Builder resultBuilder = new ImmutableList.Builder();

      // We assume that we successfully executed a query only if in at least one graph it was executed without fail.
      MisspelledNodeNameException lastMisspelledNodeNameException = null;
      IllegalArgumentException lastIllegalArgumentException = null;
      for (BindingGraph bindingGraph: bindingGraphs) {
        try {
          resultBuilder.addAll(query.execute(bindingGraph));
        } catch (IllegalArgumentException e) {
          lastIllegalArgumentException = e;
        } catch (MisspelledNodeNameException e) {
          lastMisspelledNodeNameException = e;
        }
      }

      ImmutableList resultList = resultBuilder.build();
      if (resultList.isEmpty()) {
        if (lastMisspelledNodeNameException != null) {
          throw new IllegalArgumentException(lastMisspelledNodeNameException.getMessage());
        } else if (lastIllegalArgumentException != null) {
          throw lastIllegalArgumentException;
        }
      } else {
        resultList.forEach(printStream::println);
      }
    } catch (IllegalArgumentException e) {
      printStream.println("Execution failed. Reason: " + e.getMessage());
    } catch (IOException e) {
      printStream.println("File with binding graph sources not found. Reason: " + e.getMessage());
    }
  }
}
