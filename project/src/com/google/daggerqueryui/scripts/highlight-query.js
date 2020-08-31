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

$(function (){
    $.fn.validate_query = function (query){
        return query == "deps" || query == "allpaths" || query == "somepath";
    };
});

$(document).on('keydown keyup change', '.interactive-input-container input', function (){
    if(($(this).val().length) && ($(this).val().split(' ').length) && ($(this).validate_query($(this).val().split(' ')[0]))){
        $(this).closest('.interactive-input-container').find('.query-name').html($(this).val().split(' ')[0]).show();
    } else{
        $(this).closest('.interactive-input-container').find('.query-name').hide();
    }
});
