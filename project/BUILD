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

load("@rules_java//java:defs.bzl", "java_plugin")
load("@dagger//:workspace_defs.bzl", "dagger_rules")

dagger_rules()

java_library(
    name = "dagger_query_protobuf",
    srcs = glob(["src/com/google/daggerquery/protobuf/**/*.java"]),
    deps = ["//third_party/java/protobuf:protobuf"],
)

java_plugin(
    name = "dagger_query_plugin",
    srcs = ["src/com/google/daggerquery/plugin/QueryPlugin.java"],
    deps = [
        "//:dagger-spi",
        ":dagger_query_protobuf",
        "//third_party/java/auto:service",
    ]
)

