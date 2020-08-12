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
 * A class which represents one query.
 *
 * <p>Each query has a {@code type} which itself an instance of {@link QueryType}.
 *
 * <p>Also each query contains parameters which were passed by user.
 * The number of parameters completely defined by {@link QueryType}.
 */
public class Query {
  private QueryType type;
  private String[] parameters;

  /**
   * Creates an instance of {@link Query} with specified type and parameters.
   *
   * <p>Checks that passed arguments are correct and not {@code null}. The {@code parameters} argument is
   * correct when its length equals to the {@code numberOfParameters} field specified in {@link QueryType}.
   *
   * <p>Throws {@link IllegalArgumentException} if number of parameters is invalid.
   * Throws {@link NullPointerException} if any of arguments are equal to {@code null}.
   */
  public Query(QueryType type, String... parameters) {
    if (type == null || parameters == null) {
      throw new NullPointerException("Passed arguments cannot be null.");
    } else if (type.getNumberOfParameters() != parameters.length) {
      String exceptionMessage = String.format("The number of passed parameters is incorrect. Expected: %d, got: %d.",
          type.getNumberOfParameters(), parameters.length);
      throw new IllegalArgumentException(exceptionMessage);
    }

    this.type = type;
    this.parameters = parameters;
  }

  /**
   * Returns the type of query.
   */
  public QueryType getType() {
    return type;
  }

  /**
   * Returns an array with all parameters passed by user.
   */
  public String[] getParameters() {
    return parameters;
  }
}
