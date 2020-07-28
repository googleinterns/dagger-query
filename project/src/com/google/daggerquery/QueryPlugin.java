package com.google.daggerquery;

import com.google.auto.service.AutoService;
import dagger.model.BindingGraph;
import dagger.spi.BindingGraphPlugin;
import dagger.spi.DiagnosticReporter;

@AutoService(BindingGraphPlugin.class)
public class QueryPlugin implements BindingGraphPlugin {

  @Override
  public void visitGraph(BindingGraph bindingGraph, DiagnosticReporter diagnosticReporter) {
  }

}
