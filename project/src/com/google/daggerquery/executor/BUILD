# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

package(default_visibility = ["//visibility:public"])

java_library(
     name = "query_executor",
     srcs = ["//src/com/google/daggerquery/executor:QueryExecutor.java"],
     deps = [
         "//src/com/google/daggerquery/executor/models:query_executor_models",
         "//src/com/google/daggerquery/executor/services:query_executor_services",
         "//src/com/google/daggerquery/protobuf:binding_graph_java_proto",
         "//third_party/java/guava:guava",
     ],
   )
