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
  $('[data-toggle="tooltip"]').tooltip();

  document.getElementById("physics-switch").addEventListener("mouseup", function (event) {
    const turnOnPhysicsAction = 'Turn on physics?';
    const turnOffPhysicsAction = 'Turn off physics?';
    const isPhysicsEnabled = bindingGraph.togglePhysics();

    $(this).tooltip('hide')
      .attr('data-original-title', isPhysicsEnabled ? turnOffPhysicsAction : turnOnPhysicsAction)
      .tooltip('show');

    bindingGraph.draw();
  });
})

$("#clear-icon").click(function(){
  bindingGraph.clear();
});
