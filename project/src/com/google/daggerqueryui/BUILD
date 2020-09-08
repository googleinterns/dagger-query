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

load("@io_bazel_rules_closure//closure:defs.bzl", "closure_js_library")
load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_application")

closure_js_library(
    name = "vis-js",
    srcs = ["vis-network.js"],
    suppress = [
        "lintChecks",
        "nonStandardJsDocs",
        "checkVars",
    ],
)

closure_js_library(
    name = "js_app",
    srcs = ["app.js"],
    deps = [
        "//src/com/google/daggerquery/executor/models:query_executor_models-j2cl",
        ":vis-js",
    ],
)

j2cl_application(
    name = "webapp",
    entry_points = ["daggerquery.webapp"],
    deps = [":js_app"],
)