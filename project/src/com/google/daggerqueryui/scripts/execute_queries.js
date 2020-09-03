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

$("#query-input").on('keyup', function (event) {
    if (event.key === 'Enter') {
        const userInput = document.getElementById("query-input").value.split(' ');

        try {
          console.error("1");
          const query = new Query("deps", ["com.google.Cat"]);
        } catch (error) {
          console.error("2");
          console.error(error);
        }
        console.error("3");
    }
});
