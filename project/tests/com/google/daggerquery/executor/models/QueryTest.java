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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class QueryTest {
  @Test
  public void testParsingQuery_WithNullQueryType_ThrowsNullPointerException() {
    try {
      Query query = new Query(/*type = */ null, "com.google.cats.Cat");
      fail();
    } catch (NullPointerException e) {
    }
  }

  @Test
  public void testParsingDepsQuery_WithOneStringParameter() {
    String[] parameters = {"com.google.cats.Cat"};
    Query query = new Query(QueryType.DEPS, parameters);

    assertEquals(QueryType.DEPS, query.getType());
    assertArrayEquals(parameters, query.getParameters());
  }

  @Test
  public void testParsingDepsQuery_WithTwoStringParameters_ThrowsIllegalArgumentException() {
    try {
      Query query = new Query(QueryType.DEPS, "com.google.cats.FirstCat", "com.google.cats.SecondCat");
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testParsingDepsQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    try {
      Query query = new Query(QueryType.DEPS);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testParsingDepsQuery_WithNullParameters_ThrowsNullPointerException() {
    try {
      Query query = new Query(QueryType.DEPS, /*parameters = */ null);
      fail();
    } catch (NullPointerException e) {
    }
  }
}
