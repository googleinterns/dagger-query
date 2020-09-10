package com.google.daggerquery.server;

import com.google.common.collect.ImmutableList;
import com.google.daggerquery.executor.QueryExecutor;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.Undertow;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.stream.Collectors;

public class Server {

  private static final int PORT = 4921;
  private static final String HOST = "localhost";

  /**
   * Starts a server that accepts a single <b>GET</b> request with the specified query.
   */
  public static void main(String[] args) {
    RoutingHandler routingHandler = new RoutingHandler();
    routingHandler.add("GET", "/daggerquery/{query}", Server::executeQuery);

    Undertow undertowServer = Undertow.builder()
        .addHttpListener(PORT, HOST)
        .setHandler(routingHandler)
        .build();
    undertowServer.start();
  }

  /**
   * Extracts the query from {@link HttpServerExchange} and tries to execute it.
   *
   * <p>If http response didn't contain <b>query</b> parameter then this method does nothing.
   *
   * <p>Otherwise it executes query and if it was successful sends correct subgraphs from the
   * origin binding graphs as the result.
   *
   * <p>For <b>deps</b> query returns a list with a string representation of source node's dependencies.
   * For <b>allpaths</b> and <b>somepath</b> returns a formatted {@link com.google.daggerquery.executor.models.Query.Path}
   * representation with the node names separated by an arrow.
   */
  private static void executeQuery(HttpServerExchange exchange) {
    exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
    exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET");

    Deque<String> deque = exchange.getQueryParameters().get("query");
    if (deque == null) {
      return;
    }

    String[] args = deque.stream().filter(value -> value.length() > 0).toArray(String[]::new);
    try {
      ImmutableList<String> results = QueryExecutor.execute(args);
      ByteBuffer[] buffers = results
        .stream()
        .map(result -> ByteBuffer.wrap(result.getBytes()))
        .toArray(ByteBuffer[]::new);

      exchange.setStatusCode(StatusCodes.OK);
      exchange.getResponseSender().send(buffers);
    } catch (IllegalArgumentException e) {
      exchange.setStatusCode(StatusCodes.BAD_REQUEST);
      exchange.getResponseSender().send("Execution failed. Reason: " + e.getMessage());
    } catch (IOException e) {
      exchange.setStatusCode(StatusCodes.NOT_FOUND);
      exchange.getResponseSender().send("File with binding graph sources not found. Reason: " + e.getMessage());
    }
  }
}
