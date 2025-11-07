package com.example.coolioevents.Entrant;

import com.example.coolioevents.Profile;
/**
 * Copyright 2025 Ethan Diep
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
 * This class represents an Entrant, it has a profile which
 * contains all their information
 *
 * RATIONALE:
 * This class was defined to give entrant a model representation
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-06
 */
public class Entrant {
    private Profile profile;

    public Entrant(Profile profile) {
        this.profile = profile;
    }

    /**
     * This method gets the profile of the entrant
     * @return
     *      Returns the profile of the entrant
     */
    public Profile getProfile() {
        return profile;
    }
}
