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

import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.daggerquery.protobuf.autogen.DependencyProto;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * A class which represents a query.
 *
 * <p>Each query has a String {@code name} value.
 *
 * <p>Also each query contains parameters which were passed by user.
 * The number of parameters is completely defined by the query's name.
 */
public class Query {
  /**
   * The key is the name of supported query and the value is the number of parameters,
   * required by query with such name.
   */
  private final static Map<String, Integer> supportedQueries = new HashMap<String, Integer>() {
    {
      put("deps", 1);
    }
  };

  private String name;
  private String[] parameters;

  /**
   * Creates an instance of {@link Query} with specified type and parameters.
   *
   * <p>Checks that passed arguments are correct and not {@code null}. The {@code parameters} argument is
   * correct when its length equals to the value in {@code supportedQueries}.
   *
   * <p>Throws {@link IllegalArgumentException} if number of parameters is invalid or query's name is invalid.
   * Throws {@link NullPointerException} if any of arguments are equal to {@code null}.
   */
  public Query(String typeName, String... parameters) {
    if (typeName == null || parameters == null) {
      throw new NullPointerException("Passed arguments cannot be null.");
    } else if (!supportedQueries.keySet().contains(typeName.toLowerCase())) {
      throw new IllegalArgumentException(String.format("Query with name \"%s\" isn't supported.", typeName));
    }

    name = typeName.toLowerCase();

    if (supportedQueries.get(name) != parameters.length) {
      String exceptionMessage = String.format("The number of passed parameters is incorrect. Expected: %d, got: %d.",
          supportedQueries.get(name), parameters.length);
      throw new IllegalArgumentException(exceptionMessage);
    }

    this.parameters = parameters;
  }


  /**
   * Executes query on a {@link BindingGraph}.
   *
   * <p>For all queries return a list with string. Strings content depends on a query name.
   * <p>For `deps` queries each string represent exactly one dependency.
   */
  public List<String> execute(BindingGraph bindingGraph) {
    if (name.equals("deps")) {
      String sourceNode = parameters[0];
      if (!bindingGraph.getAdjacencyListMap().containsKey(sourceNode)) {
        throw new IllegalArgumentException("Specified source node doesn't exist.");
      }

      return bindingGraph.getAdjacencyListMap().get(sourceNode).getDependencyList()
          .stream().map(DependencyProto.Dependency::getTarget).sorted().collect(toList());
    }

    throw new NotImplementedException();
  }
}
