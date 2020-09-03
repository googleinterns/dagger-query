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
const Query = goog.require('com.google.daggerquery.executor.models.Query');

/**
 * Says hello to console!
 *
 * @return {void}
 */
function print() {
  document.body.innerText = `Stuff!!!`;
  var depsQuery = new Query("deps", "com.google.Cats");
  document.body.innerText = `${depsQuery.testMethod()} is your query!`;
}

print();


