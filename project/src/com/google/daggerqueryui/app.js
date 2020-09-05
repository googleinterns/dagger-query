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

goog.module('daggerquery.webapp');

// Here we use goog.require to import the Java HelloWorld class to this module.
const GraphImpl = goog.require('com.google.daggerquery.executor.models.GraphImpl');
const Query = goog.require('com.google.daggerquery.executor.models.Query');

/* We present binding graph as an adjacency list of type Map<String, Set<String>> */
let fakeBindingGraph = new Map();

/* Fake Binding Graphs */
fakeBindingGraph.set('com.google.A', new Set(["com.google.B", "com.google.C"]));
fakeBindingGraph.set('com.google.B', new Set(["com.google.C", "com.google.D", "com.google.E"]));
fakeBindingGraph.set('com.google.C', new Set());
fakeBindingGraph.set('com.google.D', new Set(["com.google.F", "com.google.G", "com.google.H"]));
fakeBindingGraph.set('com.google.E', new Set());
fakeBindingGraph.set('com.google.F', new Set());
fakeBindingGraph.set('com.google.G', new Set(["com.google.H", "com.google.I", "com.google.J"]));
fakeBindingGraph.set('com.google.H', new Set());
fakeBindingGraph.set('com.google.I', new Set());
fakeBindingGraph.set('com.google.J', new Set());

/**
 * Says hello to console!
 *
 * @return {void}
 */
function print() {
  document.body.innerText = `Stuff!!!`;
  const bindingGraphProvider = new GraphImpl(fakeBindingGraph);
  var query = new Query("deps", "com.google.Cats");
  document.body.innerText = `${query.execute(bindingGraphProvider)} is your query!`;
}

print();


