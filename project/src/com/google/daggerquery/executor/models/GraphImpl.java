package com.google.daggerquery.executor.models;

import com.google.common.collect.ImmutableSet;

import java.util.*;

import jsinterop.annotations.JsType;

@JsType
public class GraphImpl implements Graph {
  private Map<String, Set<String>> bindingGraph;

  public GraphImpl(Map<String, Set<String>> bindingGraph) {
    this.bindingGraph = bindingGraph;
  }

  @Override
  public ImmutableSet<String> getDependencies(String node) {
    return ImmutableSet.copyOf(bindingGraph.get(node));
  }

  @Override
  public boolean containsNode(String node) {
    return bindingGraph.containsKey(node);
  }

  public boolean containsEdge(String source, String target) {
    return bindingGraph.get(source).contains(target);
  }

  @Override
  public ImmutableSet<String> getAllNodes() {
    return ImmutableSet.copyOf(bindingGraph.keySet());
  }
}
