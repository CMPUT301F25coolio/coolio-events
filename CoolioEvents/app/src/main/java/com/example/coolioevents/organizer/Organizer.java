package com.example.coolioevents.organizer;

import com.example.coolioevents.Profile;
import com.example.coolioevents.User;

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
 * This class represents an Organizer, it has a profile which
 * contains all their information.
 *
 * RATIONALE:
 * This class was defined to give organizer a model representation
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-06
 */
public class Organizer extends User {
    private Profile profile;

    /**
     * This is a constructor to make an organizer object with parameters
     * @param profile the profile to set the organizer with
     */
    public Organizer(Profile profile) {
        this.profile = profile;
    }

    /**
     * This is a constructor to make an organizer object without parameters
     */
    public Organizer() {
    }

    /**
     * This method gets the profile of the organizer
     * @return
     *      Returns the profile of the organizer
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * This method sets the profile of an organizer
     * @param profile
     *      the profile to set
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
