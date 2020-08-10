package com.google.daggerquery.executor.services;

import com.google.daggerquery.executor.models.Query;
import com.google.daggerquery.executor.models.QueryType;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * A class which is responsible for parsing user's input and
 * executing query is success.
 *
 * <p>Writes a query's result into provided {@link PrintStream} instance.
 * The format of a query's result depends on {@link QueryType}.
 */
public class DaggerQueryController {
  /**
   * Parses user's arguments and executes a query if success.
   *
   * <p>First argument must always define the name of query.
   * Next arguments belong to query as its parameters.
   *
   * <p>An instance of {@link PrintStream} specifies where an output will be written.
   */
  public void execute(String[] arguments, PrintStream printStream, SourcesLoader sourcesLoader) {
    QueryExecutor queryExecutor = new QueryExecutor();
    QueryOutputWriter outputWriter = new QueryOutputWriter(printStream);

    try {
      Query query = new Query(QueryType.parse(arguments[0]), Arrays.copyOfRange(arguments, 1, arguments.length));

      switch (query.getType()) {
        case DEPS:
          List<String> dependencies = queryExecutor.executeDepsQuery(
              query.getParameters()[0],
              sourcesLoader.loadBindingGraph()
          );
          outputWriter.writeDepsOutput(dependencies);
          break;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      printStream.println("Execution failed. You might forgot to specify the request and its parameters.");
    } catch (IllegalArgumentException e) {
      printStream.println("Execution failed. Reason: " + e.getMessage());
    } catch (IOException e) {
      printStream.println("File with binding graph sources not found. Reason: " + e.getMessage());
    }
  }
}
