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

package com.google.daggerquery.example.modules;

import com.google.daggerquery.example.models.Lifeguard;
import com.google.daggerquery.example.models.Manager;
import com.google.daggerquery.example.models.Staff;
import com.google.daggerquery.example.models.Tourist;
import dagger.Module;
import dagger.Provides;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Module
public class PeopleModule {
  @Provides
  Set<Staff> provideLifeguards(Lifeguard lifeguard1, Lifeguard lifeguard2, Lifeguard lifeguard3) {
    return new HashSet<>(Arrays.asList(lifeguard1, lifeguard2, lifeguard3));
  }

  @Provides
  List<Staff> provideManagers(Manager manager1, Manager manager2, Manager manager3, Manager manager4) {
    return Arrays.asList(manager1, manager2, manager3, manager4);
  }

  @Provides
  Set<Tourist> provideTourists(Tourist tourist1, Tourist tourist2, Tourist tourist3, Tourist tourist4) {
    return new HashSet<>(Arrays.asList(tourist1, tourist2, tourist3, tourist4));
  }
}
