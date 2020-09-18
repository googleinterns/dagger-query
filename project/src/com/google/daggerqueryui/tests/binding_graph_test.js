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
    bindingGraph.addDeps('com.google.List[com.google.A]', ['com.google.List[com.google.common.collect.ImmutableSet[com.google.A]]']);

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

  it('removes all deps and isolated nodes correctly', function() {
    bindingGraph.addDeps('com.google.A', ['com.google.B', 'com.google.C', 'com.google.D']);

    const expectedNodes = [
      [0, 'A', 'com.google.A'],
    ];

    bindingGraph.deleteDeps('com.google.A');

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]), expectedNodes);
    assert.isOk(bindingGraph.getEdges().length === 0);
  });

  it('removes all deps and keeps nodes correctly', function() {
    bindingGraph.addDeps('com.google.A', ['com.google.B']);
    bindingGraph.addDeps('com.google.C', ['com.google.B', 'com.google.D']);

    const expectedNodes = [
      [0, 'A', 'com.google.A'],
      [1, 'B', 'com.google.B'],
      [2, 'C', 'com.google.C'],
      [3, 'D', 'com.google.D'],
    ];
    const expectedEdges = [
      [2, 1],
      [2, 3],
    ];

    bindingGraph.deleteDeps('com.google.A');

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]), expectedNodes);
    assert.sameDeepMembers(bindingGraph.getEdges().map(node => [node.from, node.to]), expectedEdges);
  });

  it('removes all ancestors and isolated nodes correctly', function() {
    bindingGraph.addDeps('com.google.A', ['com.google.B']);
    bindingGraph.addDeps('com.google.C', ['com.google.B']);
    bindingGraph.addDeps('com.google.D', ['com.google.B']);

    const expectedNodes = [
      [1, 'B', 'com.google.B'],
    ];

    bindingGraph.deleteAncestors('com.google.B');

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]), expectedNodes);
    assert.isOk(bindingGraph.getEdges().length === 0);
  });

  it('removes all ancestors and keeps nodes correctly', function() {
    bindingGraph.addDeps('com.google.A', ['com.google.B', 'com.google.C']);
    bindingGraph.addDeps('com.google.C', ['com.google.B']);

    const expectedNodes = [
      [0, 'A', 'com.google.A'],
      [1, 'B', 'com.google.B'],
      [2, 'C', 'com.google.C'],
    ];
    const expectedEdges = [
      [0, 2],
    ];

    bindingGraph.deleteAncestors('com.google.B');

    assert.sameDeepMembers(bindingGraph.getNodes().map(node => [node.id, node.label, node.title]), expectedNodes);
    assert.sameDeepMembers(bindingGraph.getEdges().map(node => [node.from, node.to]), expectedEdges);
  });
});
