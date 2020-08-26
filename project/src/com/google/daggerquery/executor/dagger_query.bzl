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

load("@rules_java//java:defs.bzl", "java_binary")
load("//src/com/google/daggerquery/plugin:dagger_query_textproto.bzl", "dagger_query_textproto")

def dagger_query(name, dagger_app_target):
   dagger_query_textproto(
       name = "binding_graph_data",
       dagger_app_target = dagger_app_target
   )

   java_binary(
     name = name,
     main_class = "com.google.daggerquery.executor.QueryExecutor",
     runtime_deps = ["//src/com/google/daggerquery/executor:query_executor"],
     resources = [":binding_graph_data"],
   )
