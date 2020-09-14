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
  $.supportedQueries = new Map([
    ['deps', 1],
    ['allpaths', 2],
    ['somepath', 2],
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

$(function () {
  /**
   * Sends a request to the server with specified query and gets a response.
   * @param url an address with the correct path and request parameters in it
   * @return {Promise<any>} a promise that contains a json with a graph on success
   */
  $.fn.getQueryResults = async function (url) {
    let response = await fetch(url);

    if (response.status === 200) {
      return await response.json();
    }

    throw await response.text();
  };

  /**
   * Processes a valid query by sending a request to the specified URL,
   * receiving a response, and performing UI actions in response.
   *
   * @param url an address with the correct path and request parameters in it
   */
  $.fn.processQuery = async function (url) {
    try {
      const graph = await $(this).getQueryResults(url);
      $(this).markInputFieldAsValid();
    } catch (error) {
      $(this).markInputFieldAsInvalid(error);
    }
  }
});

$("#query-input").on('keydown keyup change', function (event) {
  const query = $(this).val().trim().split(' ');
  const queryName = query[0];

  const queryNameElement = $(this).closest('.interactive-input-container').find('.query-name');
  if (!$(this).validateQueryName(queryName)) {
    queryNameElement.hide();
    return;
  }

  // Highlights query's name when it is valid.
  queryNameElement.html(queryName).show();

  if (event.key === 'Enter' && $(this).validateParameters(query)) {
    const url = new URL(`http://localhost:4921/daggerquery/`);
    url.searchParams.append('query', query.join(' '));

    $(this).processQuery(url);
  }
});
