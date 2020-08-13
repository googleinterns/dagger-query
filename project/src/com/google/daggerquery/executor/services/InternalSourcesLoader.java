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

import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class InternalSourcesLoader implements SourcesLoader {
  private static final String PATH_TO_BINDING_GRAPH = "/com/google/daggerquery/binding_graph_data.textproto";

  public BindingGraph loadBindingGraph() throws IOException {
    InputStream inputStream = InternalSourcesLoader.class.getResourceAsStream(PATH_TO_BINDING_GRAPH);

    if (inputStream == null) {
      throw new FileNotFoundException(String.format("File %s is missing.", PATH_TO_BINDING_GRAPH));
    }

    return BindingGraph.parseFrom(inputStream);
  }
}
