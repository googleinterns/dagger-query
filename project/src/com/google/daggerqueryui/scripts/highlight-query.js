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
    $.fn.validateQuery = function (query) {
        var supportedQueries = ["deps", "allpaths", "somepath"];
        return supportedQueries.includes(query);
    };
});

$(document).on('keydown keyup change', '.interactive-input-container input', function () {
    const query = $(this).val().split(' ')[0];
    if ($(this).validateQuery(query)) {
        $(this).closest('.interactive-input-container').find('.query-name').html(query).show();
    } else {
        $(this).closest('.interactive-input-container').find('.query-name').hide();
    }
});
