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

import com.google.daggerquery.executor.models.Query;
import com.google.daggerquery.executor.services.SourcesLoader;

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
        throw new IllegalArgumentException("You might forgot to specify the request and its parameters.");
      }

      Query query = new Query(args[0], Arrays.copyOfRange(args, 1, args.length));
      List<String> result = query.execute(new SourcesLoader().loadBindingGraph());
      result.forEach(printStream::println);
    } catch (IllegalArgumentException e) {
      printStream.println("Execution failed. Reason: " + e.getMessage());
    } catch (IOException e) {
      printStream.println("File with binding graph sources not found. Reason: " + e.getMessage());
    }
  }
}
