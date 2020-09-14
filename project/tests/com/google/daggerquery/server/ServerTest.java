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

package com.google.daggerquery.server;

import org.apache.http.client.utils.URIBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ServerTest {

  private static URIBuilder uriBuilder;

  @BeforeClass
  public static void startServer() {
    // We can pass null since these arguments are not used.
    Server.main(/*args =*/ null);

    uriBuilder = new URIBuilder()
      .setScheme("http")
      .setHost(Server.HOST)
      .setPort(Server.PORT)
      .setPath(Server.PATH);
  }

  @Test
  public void testExecutingQuery_WhenGraphNotFound_Returns404StatusCode() {
    try {
      URL url = uriBuilder
        .addParameter("query", "deps com.google.Cat")
        .build()
        .toURL();

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      int statusCode = connection.getResponseCode();
      BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

      String expectedResponseMessage = "File with binding graph sources not found. " +
        "Reason: File /com/google/daggerquery/binding_graph_data.zip is missing.";
      assertEquals(404, statusCode);
      assertEquals(expectedResponseMessage, stream.readLine());
    } catch (URISyntaxException | IOException e) {
      fail();
    }
  }

  @AfterClass
  public static void stopServer() {
    Server.stop();
  }
}
