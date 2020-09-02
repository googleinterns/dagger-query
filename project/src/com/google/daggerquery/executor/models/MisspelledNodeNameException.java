package com.google.daggerquery.executor.models;

import java.util.List;

/**
 * An exception is thrown to indicate that a binding name contains a typo.
 *
 * <p>This exception <b>should</b> be used to report a typo in a binding node name,
 * which user has provided as a query parameter.
 */
public class MisspelledNodeNameException extends RuntimeException {

  /**
   * Constructs a <code>MisspelledNodeNameException</code> with a detailed message
   * consisting of the provided binding name, followed by similarly named bindings
   * that exist in our binding graph.
   */
  public MisspelledNodeNameException(String nodeNameWithTypo, List<String> correctNodesName) {
    super("Binding with name "
            + nodeNameWithTypo
            + " contains a typo and not found in a graph. Maybe you meant "
            + String.join(" or ", correctNodesName)
            + "?");
  }

}
