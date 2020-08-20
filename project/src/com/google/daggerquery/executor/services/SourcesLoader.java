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

package com.google.daggerquery.executor.services;

import com.google.common.io.Files;
import com.google.daggerquery.protobuf.autogen.BindingGraphProto.BindingGraph;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A class which loads a binding graph saved with Dagger SPI plugin.
 */
public class SourcesLoader {
  private static final String PATH_TO_BINDING_GRAPHS = "/com/google/daggerquery/binding_graph_data.zip";
  private static final String BINDING_GRAPHS_SOURCES = "binding_graphs.tmp";

  /**
   * Reads .zip resource file which contains several .textproto files with serialized binding graphs.
   *
   * <p>Returns a list with {@link BindingGraph} instances. Parses them from files located in .zip file.
   *
   * @throws FileNotFoundException if an app with the connected plugin wasn't launched and .zip file cannot be found
   * @throws IOException if an I/O error occurred while extracting files from .zip file
   */
  public List<BindingGraph> loadBindingGraphs() throws IOException {
    try (InputStream zipInputStream = SourcesLoader.class.getResourceAsStream(PATH_TO_BINDING_GRAPHS)) {
      if (zipInputStream == null) {
        throw new FileNotFoundException(String.format("File %s is missing.", PATH_TO_BINDING_GRAPHS));
      }

      File fileWithSources = makeFileFromInputStream(zipInputStream);
      try (ZipFile zipFile = new ZipFile(fileWithSources)) {
        Enumeration<? extends ZipEntry> filesWithBindingGraphs = zipFile.entries();

        List<BindingGraph> bindingGraphs = new ArrayList<>();
        while (filesWithBindingGraphs.hasMoreElements()) {
          ZipEntry bindingGraphEntry = filesWithBindingGraphs.nextElement();

          try {
            try (InputStream inputStream = zipFile.getInputStream(bindingGraphEntry)) {
              bindingGraphs.add(BindingGraph.parseFrom(inputStream));
            }
          } catch (InvalidProtocolBufferException e) {
            // This might happened because non-proto files were in .zip.
            // However, we still can parse other files with serialized binding graphs and ignore this exception.
          }
        }

        if (bindingGraphs.isEmpty()) {
          throw new FileNotFoundException("The .zip does not contain .textproto files with serialized binding graphs.");
        }

        fileWithSources.delete();
        return bindingGraphs;
      }
    }
  }

  /**
   * Copies all data from given {@link InputStream} into {@link File}.
   *
   * <p>The need for this mapping arises from the fact that we should access the resource
   * as a stream when the resource is bundled as a .zip file. In general we convert {@link InputStream}
   * instance into {@link File} to access all .textproto zipped files.
   *
   * @throws IOException if an I/O error occurred while reading sources from given {@link InputStream} instance
   */
  private File makeFileFromInputStream(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[inputStream.available()];
    inputStream.read(buffer);

    // Creates a new temporary file and fills its content.
    File fileWithSources = new File(BINDING_GRAPHS_SOURCES);
    fileWithSources.createNewFile();
    Files.write(buffer, fileWithSources);

    return fileWithSources;
  }
}
