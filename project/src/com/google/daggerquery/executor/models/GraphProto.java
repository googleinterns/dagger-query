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

import com.google.common.collect.ImmutableSet;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.daggerquery.protobuf.autogen.DependencyProto.Dependency;

import static java.util.stream.Collectors.toSet;

/**
 * Implementation of {@link Graph} that wraps the {@link BindingGraph} generated
 * with <a href="https://developers.google.com/protocol-buffers">protocol buffers library</a>.
 */
public class GraphProto implements Graph {

  private BindingGraph bindingGraph;

  public GraphProto(BindingGraph bindingGraph) {
    this.bindingGraph = bindingGraph;
  }

  @Override
  public ImmutableSet<String> getDependencies(String node) {
    return ImmutableSet.copyOf(bindingGraph.getAdjacencyListMap().get(node).getDependencyList()
        .stream().map(Dependency::getTarget).sorted().collect(toSet()));
  }

  @Override
  public boolean containsNode(String node) {
    return bindingGraph.getAdjacencyListMap().containsKey(node);
  }

  @Override
  public ImmutableSet<String> getAllNodes() {
    return ImmutableSet.copyOf(bindingGraph.getAdjacencyListMap().keySet());
  }
}
