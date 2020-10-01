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

import javax.inject.Inject;

public class Apartment {
  private final Bed bed;
  private final MiniBar miniBar;
  private final TV tv;
  private final Phone phone;
  private final Tourist tourist;

  @Inject
  Apartment(Bed bed, MiniBar miniBar, TV tv, Phone phone, Tourist tourist) {
    this.bed = bed;
    this.miniBar = miniBar;
    this.tv = tv;
    this.phone = phone;
    this.tourist = tourist;
  }
}
