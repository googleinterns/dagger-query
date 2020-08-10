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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class QueryTypeTest {
  @Test
  public void testParsingDepsQueryType_FromValidString() {
    QueryType type = QueryType.parse("deps");

    assertEquals(QueryType.DEPS, type);
  }

  @Test
  public void testParsingDepsQueryType_FromValidString_WithCapitalizedLetters() {
    QueryType type = QueryType.parse("DePs");

    assertEquals(QueryType.DEPS, type);
  }

  @Test
  public void testParsingDepsQueryType_FromInvalidString_ThrowsIllegalArgumentException() {
    try {
      QueryType type = QueryType.parse("_deps");
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testParsingDepsQueryType_FromNull_ThrowsIllegalArgumentException() {
    try {
      QueryType type = QueryType.parse(null);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }
}
