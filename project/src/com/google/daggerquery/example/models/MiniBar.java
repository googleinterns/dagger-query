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

package com.google.daggerquery.example.models;

import javax.inject.Inject;

public class MiniBar {
  private final CocaCola cocaCola;
  private final Fanta fanta;
  private final Sprite sprite;

  @Inject
  MiniBar(CocaCola cocaCola, Fanta fanta, Sprite sprite) {
    this.cocaCola = cocaCola;
    this.fanta = fanta;
    this.sprite = sprite;
  }
}
