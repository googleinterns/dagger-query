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

package com.google.daggerquery.example;

import com.google.daggerquery.example.models.Tourist;
import com.google.daggerquery.example.models.Beach;
import com.google.daggerquery.example.modules.PeopleModule;
import dagger.Component;
import dagger.Module;
import dagger.Subcomponent;

@Subcomponent
interface TouristsComponent {
  Tourist getTourist();

  @Subcomponent.Builder
  interface Builder {
    TouristsComponent build();
  }
}

@Module(subcomponents = TouristsComponent.class)
class TouristsModule {
}

@Component(modules = {PeopleModule.class, TouristsModule.class})
public interface BeachComponent {
  Beach getBeach();
}
