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
  @Test(expected = NullPointerException.class)
  public void testParsingQuery_WithNullQueryType_ThrowsNullPointerException() {
    Query query = new Query(/*type = */ null, "com.google.cats.Cat");
  }

  @Test
  public void testParsingDepsQuery_WithOneStringParameter() {
    String[] parameters = {"com.google.cats.Cat"};
    Query query = new Query(QueryType.DEPS, parameters);

    assertEquals(QueryType.DEPS, query.getType());
    assertArrayEquals(parameters, query.getParameters());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingDepsQuery_WithTwoStringParameters_ThrowsIllegalArgumentException() {
    Query query = new Query(QueryType.DEPS, "com.google.cats.FirstCat", "com.google.cats.SecondCat");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingDepsQuery_WithoutParameters_ThrowsIllegalArgumentException() {
    Query query = new Query(QueryType.DEPS);
  }

  @Test(expected = NullPointerException.class)
  public void testParsingDepsQuery_WithNullParameters_ThrowsNullPointerException() {
    Query query = new Query(QueryType.DEPS, /*parameters = */ null);
  }
}
