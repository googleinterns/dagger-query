// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var assert = chai.assert;

describe('Binding Graph', function() {
  beforeEach(function () {
    bindingGraph.clear();
  });

  it('adds one edge correctly', function() {
    bindingGraph.addDeps('com.google.A', ['com.google.B']);

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]),
                           [[0, 'A', 'com.google.A'], [1, 'B', 'com.google.B']]);
    assert.sameDeepMembers(bindingGraph.getEdges().map(node => [node.from, node.to]), [[0, 1]]);
  });

  it('extracts the simple name from the complex node name correctly', function() {
    bindingGraph.addDeps('com.google.List<com.google.A>', ['com.google.List<com.google.common.collect.ImmutableSet<com.google.A>>']);

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]),
                           [[0, 'List[A]', 'com.google.List[com.google.A]'], [1, 'List[ImmutableSet[A]]', 'com.google.List[com.google.common.collect.ImmutableSet[com.google.A]]']]);
  });

  it('adds one path correctly', function() {
    bindingGraph.addPath('com.google.A -> com.google.B -> com.google.C');

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]),
                           [[0, 'A', 'com.google.A'], [1, 'B', 'com.google.B'], [2, 'C', 'com.google.C']]);
    assert.sameDeepMembers(bindingGraph.getEdges().map(node => [node.from, node.to]),
                           [[0, 1], [1, 2]]);
  });

  it('adds multiple paths correctly', function() {
    bindingGraph.addPath('com.google.A -> com.google.B -> com.google.C');
    bindingGraph.addPath('com.google.B -> com.google.D -> com.google.E');

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]),
                           [[0, 'A', 'com.google.A'], [1, 'B', 'com.google.B'], [2, 'C', 'com.google.C'], [3, 'D', 'com.google.D'], [4, 'E', 'com.google.E']]);
    assert.sameDeepMembers(bindingGraph.getEdges().map(node => [node.from, node.to]),
                           [[0, 1], [1, 2], [1, 3], [3, 4]]);
  });
});
