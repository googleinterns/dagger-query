# Dagger Query

A graph based query language on top of [dagger](https://github.com/google/dagger)

Dagger Query can be used to investigate the dagger dependency graph.

**This is not an officially supported Google product.**


## How to use Dagger Query?

1. Add `//src/com/google/daggerquery:dagger_query_plugin` target in a **plugins** attribute of your app's target.
2. Use **dagger_query** rule located in a `src/com/google/daggerquery/executor` with your app's target name. 

       dagger_query(
          name = "dagger_query_app",

          # Put here the name of a target in which you have used a plugin for generating binding graph.
          dagger_app_target = "YOUR_TARGET"
       )

3. Use bash script `dagger-query.sh` located in a `project` folder for executing queries! 🚀 Pass a path to the `dagger_query` target as the first parameter.  
    > ./dagger-query.sh YOUR_PATH deps com.google.Cat \
    > ./dagger-query.sh YOUR_PATH allpaths com.google.Office com.google.Beach \
    > ./dagger-query.sh YOUR_PATH somepath com.google.Paris com.google.sights.EiffelTower
    
## How to use Dagger Query UI?

1. Add `//src/com/google/daggerquery:dagger_query_plugin` target in a **plugins** attribute of your app's target.
2. Use **dagger_query_server** rule located in a `src/com/google/daggerquery/server` with your app's target name. 

       dagger_query_server(
          name = "dagger_query_ui_server",

          # Put here the name of a target in which you have used a plugin for generating binding graph.
          dagger_app_target = "YOUR_TARGET"
       )

3. Use bash script `dagger-query-ui.sh` located in a `project` folder for starting a server and opening a website! 👾 Pass a path to the `dagger_query_server` target as the parameter.  
    > ./dagger-query-ui.sh YOUR_PATH 

## Dagger Query UI: Getting started

### Using an example project
If you launch `dagger-query-ui.sh` script without any parameters it will use example target by default. It will open a website where you can specify queries in the input field.
```
cd project
./dagger-query-ui.sh
```
### Executing queries

> allpaths com.google.daggerquery.example.HotelComponent com.google.daggerquery.example.models.Phone

![allpaths](https://github.com/googleinterns/dagger-query/blob/improve_readme/project/assets/allpaths_query_example.png)

> somepath com.google.daggerquery.example.BeachComponent com.google.daggerquery.example.models.Tourist

![somepath](https://github.com/googleinterns/dagger-query/blob/improve_readme/project/assets/somepath_query_example.png)

> deps com.google.daggerquery.example.models.Villa 

![deps](https://github.com/googleinterns/dagger-query/blob/improve_readme/project/assets/deps_query_example.png)

> rdeps com.google.daggerquery.example.models.Tourist 

![rdeps](https://github.com/googleinterns/dagger-query/blob/improve_readme/project/assets/rdeps_query_example.png)

### Traversing an arbitrary graph

* Enter a node name in the input field and it will appear on the canvas if such a node exists on the graph.
* Use **left-click** on a node to show all its dependencies and hide them.
* Use **right-click** on a node to show all its ancestors and hide them.

![traversing](https://github.com/googleinterns/dagger-query/blob/improve_readme/project/assets/traversing_graph.gif)

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
