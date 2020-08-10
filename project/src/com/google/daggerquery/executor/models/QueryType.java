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

/**
 * An enum which represents type of query, which can be executed via traversing binding graph.
 *
 * <p>Each query identified by its name and number of parameters it takes.
 * This data associated with {@link QueryType} instance.
 */
public enum QueryType {
  DEPS("deps", 1);

  private String name;
  private int numberOfParameters;

  QueryType(String queryName, int numberOfParameters) {
    this.name = queryName;
    this.numberOfParameters = numberOfParameters;
  }

  /**
   * Returns the number of parameters required to execute a query of a specific type.
   */
  public int getNumberOfParameters() {
    return numberOfParameters;
  }

  /**
   * Parses given string representation of query to instance of enum {@link QueryType}.
   *
   * <p>Parsing isn't case sensitive. For success, provided string must be equal to
   * {@code name} field of one of the query types presented in {@link QueryType}.
   *
   * <p>If match cannot be found throws {@link IllegalArgumentException}.
   */
  public static QueryType parse(String query) {
    for (QueryType queryType: QueryType.values()) {
      if (queryType.name.equalsIgnoreCase(query)) {
        return queryType;
      }
    }

    throw new IllegalArgumentException("Query with given name isn't implemented.");
  }
}
