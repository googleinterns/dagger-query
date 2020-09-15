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

import com.google.daggerquery.example.models.Apartment;
import com.google.daggerquery.example.models.Villa;
import dagger.Module;
import dagger.Provides;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Module
public class AccommodationModule {
  @Provides
  Set<Apartment> provideApartments(Apartment apartment1, Apartment apartment2, Apartment apartment3) {
    return new HashSet<>(Arrays.asList(apartment1, apartment2, apartment3));
  }

  @Provides
  Map<Villa, Integer> provideVillas(Villa villa1, Villa villa2, Villa villa3, Villa villa4, Villa villa5) {
    Map<Villa, Integer> map = new HashMap<>();
    map.put(villa1, 100);
    map.put(villa2, 200);
    map.put(villa3, 150);
    map.put(villa4, 170);
    map.put(villa5, 130);

    return map;
  }
}
