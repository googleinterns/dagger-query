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

import java.io.PrintStream;
import java.util.List;

public class QueryOutputWriter {
  private PrintStream printStream;

  /**
   * Creates an instance of {@link QueryOutputWriter} and
   * associates it with the given {@link PrintStream} instance,
   * where all output will be written.
   */
  public QueryOutputWriter(PrintStream printStream) {
    this.printStream = printStream;
  }

  /**
   * Writes an output of execution {@link com.google.daggerquery.executor.models.QueryType}.DEPS query.
   *
   * <p>The output is an array with all dependencies presented as Strings,
   * so this method simply writes them one by one.
   */
  public void writeDepsOutput(List<String> dependencies) {
    dependencies.forEach(printStream::println);
  }
}
