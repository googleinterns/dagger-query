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

/**
 * The interface representing the graph created by the Dagger SPI plugin.
 *
 * <p>Provides the methods needed to traverse this graph and to execute queries.
 */
public interface Graph {

  /**
   * Returns all node's dependencies.
   */
  ImmutableSet<String> getDependencies(String node);

  /**
   * Returns all of a node's ancestors (nodes that depend on this node).
   */
  ImmutableSet<String> getAncestors(String node);

  /**
   * Checks if a node is presented in the graph or not.
   */
  boolean containsNode(String node);

  /**
   * Returns all the nodes presented in the graph.
   */
  ImmutableSet<String> getAllNodes();

}
