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
load("@dagger//:workspace_defs.bzl", "dagger_rules")

dagger_rules();
package(default_visibility = ["//visibility:public"])

java_binary(
    name = "beach_example",
    srcs = glob(["*.java"]),
    main_class = "com.google.daggerquery.example.Main",
    deps = [
        ":dagger",
        "//src/com/google/daggerquery:dagger_query_plugin"
    ],
    plugins = ["//src/com/google/daggerquery:dagger_query_plugin"]
)
