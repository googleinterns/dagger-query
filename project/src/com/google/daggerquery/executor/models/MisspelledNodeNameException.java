package com.google.daggerquery.executor.models;

/**
 * An exception is thrown to indicate that a binding name contains a typo.
 *
 * <p>This exception <b>should</b> be used to report a typo in a binding node name,
 * which user has provided as a query parameter.
 */
public class MisspelledNodeNameException extends RuntimeException {

  /**
   * Constructs a <code>MisspelledNodeNameException</code> with a detailed message
   * consisting of the given binding name with typo followed by the correct binding name
   * which presented in a binding graph.
   */
  public MisspelledNodeNameException(String nodeNameWithTypo, String correctNodeName) {
    super(String.format("Binding with name %s contains a typo and not found in a graph. Maybe you meant %s?",
                        nodeNameWithTypo, correctNodeName));
  }

}
