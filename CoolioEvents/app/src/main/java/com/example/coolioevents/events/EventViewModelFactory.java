/**
 * Copyright 2025 Juliane Phan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class represents a factory for the EventViewModel, which is required to instantiate
 * ViewModels that take in parameters.
 * In our case, the EventViewModel takes in a database (db) as its parameter.
 *
 * RATIONALE:
 * This class was designed to support instantiation of EventViewModel (which takes in a parameter).
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-06
 */

package com.example.coolioevents.events;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.google.firebase.firestore.FirebaseFirestore;

// Source - https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
// Posted by mlykotom
// Retrieved by Juliane Phan on 2025-11-06, License - CC BY-SA 4.0
// Used to implement the factory class for our EventViewModel
// Modifications made: Used our own class and parameter names
public class EventViewModelFactory implements ViewModelProvider.Factory {
    private final FirebaseFirestore db;

    public EventViewModelFactory(FirebaseFirestore db) {
        this.db = db;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new EventViewModel(db);
    }
}
