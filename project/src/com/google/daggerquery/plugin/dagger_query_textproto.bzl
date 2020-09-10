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

def dagger_query_textproto(name, dagger_app_target):
    _dagger_query_textproto(name = name, dagger_app_target = dagger_app_target)

def _dagger_query_textproto_impl(ctx):
    dagger_app_target = ctx.attr.dagger_app_target
    src_jars = dagger_app_target[JavaInfo].source_jars

    if len(src_jars) != 1:
        fail("Found multiple jars with sources")

    ctx.actions.run_shell(
        inputs = src_jars,
        outputs = [ctx.outputs.out],
        command = "unzip {src_jar} '*_graph.textproto' -d binding_graphs_tmp; cd binding_graphs_tmp; zip -r ../{out} .; cd ..; rm -r binding_graphs_tmp".format(
            src_jar = src_jars[0].path,
            out = ctx.outputs.out.path,
        ),
    )

_dagger_query_textproto = rule(
    attrs = {
        "dagger_app_target": attr.label(
            mandatory = True,
            providers = [JavaInfo]
        ),
    },
    outputs = {
        "out": "binding_graph_data.zip",
    },
    implementation = _dagger_query_textproto_impl,
)
