# Dagger Query

A graph based query language on top of [dagger](https://github.com/google/dagger)

Dagger Query can be used to investigate the dagger dependency graph.


## How to use Dagger Query project?

1. Add `//src/com/google/daggerquery:dagger_query_plugin` target in a **plugins** attribute of your app's target.
2. Go to `src/com/google/daggerquery/BUILD` and use **dagger_query** rule with your app's target name. 

       dagger_query(
          name = "dagger_query_app",

          # Put here the name of a target in which you have used a plugin for generating binding graph.
          dagger_app_target = "YOUR_TARGET"
       )

3. Use bash script located in a `project` folder for executing queries! 🚀 
    > ./dagger-query.sh deps com.google.Cat \
    > ./dagger-query.sh allpaths com.google.Office com.google.Beach \
    > ./dagger-query.sh somepath com.google.Paris com.google.sights.EiffelTower


**This is not an officially supported Google product.**

## Source Code Headers

Every file containing source code must include copyright and license
information. This includes any JS/CSS files that you might be serving out to
browsers. (This is to help well-intentioned people avoid accidental copying that
doesn't comply with the license.)

Apache header:

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
