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

package com.google.daggerquery.plugin;

import com.google.auto.service.AutoService;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto;
import dagger.model.BindingGraph;
import dagger.spi.BindingGraphPlugin;
import dagger.spi.DiagnosticReporter;
import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;

@AutoService(BindingGraphPlugin.class)
public class QueryPlugin implements BindingGraphPlugin {

  // An instance used to create a new resource file at compile time.
  private Filer filer;

  @Override
  public void initFiler(Filer filer) {
    this.filer = filer;
  }

  @Override
  public void visitGraph(BindingGraph bindingGraph, DiagnosticReporter diagnosticReporter) {
    BindingGraphProto.BindingGraph bindingGraphProto = new GraphConverter<BindingGraph.Node, BindingGraph.Edge>()
        .makeBindingGraphProto(bindingGraph.rootComponentNode(), bindingGraph.network());

    try {
      FileObject sourceFile = filer.createResource(SOURCE_OUTPUT, "", "binding_graph.textproto");
      try (OutputStream outputStream = sourceFile.openOutputStream()) {
        byte[] bindingGraphSize = ByteBuffer.allocate(4).putInt(bindingGraphProto.toByteArray().length).array();
        outputStream.write(bindingGraphSize);
        bindingGraphProto.writeTo(outputStream);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
