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

$(function () {
  $.DEPS_QUERY_NAME = 'deps';
  $.SOMEPATH_QUERY_NAME = 'somepath';
  $.ALLPATHS_QUERY_NAME = 'allpaths';
  $.RDEPS_QUERY_NAME = 'rdeps';
  $.EXISTS_QUERY_NAME = 'exists';

  $.supportedQueries = new Map([
    [$.DEPS_QUERY_NAME, 1],
    [$.ALLPATHS_QUERY_NAME, 2],
    [$.SOMEPATH_QUERY_NAME, 2],
    [$.RDEPS_QUERY_NAME, 1],
    [$.EXISTS_QUERY_NAME, 1],
  ]);

  /**
   * Validates the request name to see if it is supported or not.
   *
   * @param {string} queryName the name of the query to check
   * @return {boolean} a value that determines whether the request is supported or not
   */
  $.fn.validateQueryName = function (queryName) {
    return $.supportedQueries.has(queryName);
  };

  /**
   * Validates the number of query parameters.
   *
   * @param {Array<string>} query a full query passed by user with unique name and parameters
   * @return {boolean} a value that determines whether the number of parameters is correct or not
   */
  $.fn.validateParameters = function (query) {
    return $.supportedQueries.get(query[0]) === query.length - 1;
  };
});

$(function () {
  /**
   * Marks input field as invalid and shows a container with error message.
   * @param {string} errorMessage a error message which will be shown under the input field
   */
  $.fn.markInputFieldAsInvalid = function (errorMessage) {
    $("#query-input").removeClass("is-valid").addClass("is-invalid");
    $("#error-message").removeClass("hidden").text(errorMessage);
  };

  /**
   * Marks the input field as valid and hides the container with an error message if shown earlier.
   */
  $.fn.markInputFieldAsValid = function () {
    $("#query-input").removeClass("is-invalid").addClass("is-valid");
    $("#error-message").addClass("hidden");
  };
});

const queryExecutor = (function() {
  /**
   * Sends a request to the server with specified query and gets a response.
   * @param url an address with the correct path and request parameters in it
   * @return {Promise<any>} a promise that contains a json with a graph on success
   */
  async function getQueryResults(url) {
    let response = await fetch(url);

    if (response.status === 200) {
      return await response.json();
    }

    throw await response.text();
  }

  return {
    /**
     * Processes a valid query by sending a request to the specified URL,
     * receiving a response, and performing UI actions in response.
     *
     * @param {string[]} query a valid query which will be executed
     * @param {boolean} shouldClearGraph a flag which indicates if the graph should be cleaned or not
     */
    processQuery: async function(query, {shouldClearGraph}) {
      try {
        const url = new URL(`http://localhost:4921/daggerquery/`);
        url.searchParams.append('query', query.join(' '));
        const results = await getQueryResults(url);
        $(this).markInputFieldAsValid();

        if (shouldClearGraph) {
          bindingGraph.clear();
        }

        if (query[0] === $.DEPS_QUERY_NAME) {
          bindingGraph.addDeps(query[1], results);
        } else if (query[0] === $.ALLPATHS_QUERY_NAME || query[0] === $.SOMEPATH_QUERY_NAME) {
          for (const path of results) {
            bindingGraph.addPath(path);
          }
        } else if (query[0] === $.RDEPS_QUERY_NAME) {
          bindingGraph.addAncestors(query[1], results);
        } else if (query[0] === $.EXISTS_QUERY_NAME) {
          bindingGraph.addNode(query[1]);
        }

        bindingGraph.draw();
      } catch (error) {
        $(this).markInputFieldAsInvalid(error);
      }
    }
  };
})();

$("#query-input").on('keyup', function (event) {
  const query = $(this).val().trim().split(' ');
  const queryName = query[0].toLowerCase();

  const queryNameElement = $(this).closest('.interactive-input-container').find('.query-name');
  if (!$(this).validateQueryName(queryName)) {
    queryNameElement.hide();

    // The user can specify the node name without the query name.
    // If such a node exists in the graph, it will be drawn.
    if (event.key === 'Enter' && query.length === 1) {
      queryExecutor.processQuery([$.EXISTS_QUERY_NAME, query[0]], {shouldClearGraph: false});
    }

    return;
  }

  // Highlights query's name when it is valid.
  queryNameElement.html(queryName).show();

  if (event.key === 'Enter') {
    if ($(this).validateParameters(query)) {
      queryExecutor.processQuery(query, {shouldClearGraph: true});
    } else {
      $(this).markInputFieldAsInvalid(
        `The number of passed parameters for ${queryName} query is incorrect.` +
        ` Expected: ${$.supportedQueries.get(queryName)}, got: ${query.length - 1}.`
      );
    }
  }
});
