# Copyright (C) 2018 The Google Bazel Common Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""A WORKSPACE macro for Google open-source libraries to use"""

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:java.bzl", "java_import_external")

_MAVEN_MIRRORS = [
    "https://bazel-mirror.storage.googleapis.com/repo1.maven.org/maven2/",
    "https://repo1.maven.org/maven2/",
]

def _maven_import(artifact, sha256, licenses, **kwargs):
    parts = artifact.split(":")
    group_id = parts[0]
    artifact_id = parts[1]
    version = parts[2]
    name = ("%s_%s" % (group_id, artifact_id)).replace(".", "_").replace("-", "_")
    url_suffix = "{0}/{1}/{2}/{1}-{2}.jar".format(group_id.replace(".", "/"), artifact_id, version)

    java_import_external(
        name = name,
        jar_urls = [base + url_suffix for base in _MAVEN_MIRRORS],
        jar_sha256 = sha256,
        licenses = licenses,
        rule_load = """load("@rules_java//java:defs.bzl", "java_import")""",
        tags = ["maven_coordinates=" + artifact],
        **kwargs
    )

def google_common_workspace_rules():
    """Defines WORKSPACE rules for Google open-source libraries.

    Call this once at the top of your WORKSPACE file to load all of the repositories. Note that you
    should not refer to these repositories directly and instead prefer to use the targets defined in
    //third_party.
    """

    # Import AutoService

    _maven_import(
        artifact = "com.google.auto:auto-common:0.10",
        licenses = ["notice"],
        sha256 = "b876b5fddaceeba7d359667f6c4fb8c6f8658da1ab902ffb79ec9a415deede5f",
    )

    _maven_import(
        artifact = "com.google.auto.service:auto-service:1.0-rc4",
        licenses = ["notice"],
        sha256 = "e422d49c312fd2031222e7306e8108c1b4118eb9c049f1b51eca280bed87e924",
    )

    # Import Protocol Buffers

    _maven_import(
        artifact = "com.google.protobuf:protobuf-java:3.12.0",
        licenses = ["notice"],
        sha256 = "a98ed5a0272cdda6bde98fe15e794e3d99ec04554dba0ba8e0a49ff5cecc5e9e",
    )

    for protobuf_repo in ("com_google_protobuf", "com_google_protobuf_java"):
        http_archive(
            name = protobuf_repo,
            sha256 = "c5dc4cacbb303d5d0aa20c5cbb5cb88ef82ac61641c951cdf6b8e054184c5e22",
            strip_prefix = "protobuf-3.12.4",
            urls = ["https://github.com/protocolbuffers/protobuf/archive/v3.12.4.zip"],
        )

    # Guava

    _maven_import(
        artifact = "com.google.guava:guava:27.1-jre",
        licenses = ["notice"],
        sha256 = "4a5aa70cc968a4d137e599ad37553e5cfeed2265e8c193476d7119036c536fe7",
    )

    # JUnit 4

    _maven_import(
        artifact = "junit:junit:4.13",
        licenses = ["notice"],
        sha256 = "4b8532f63bdc0e0661507f947eb324a954d1dbac631ad19c8aa9a00feed1d863",
    )

