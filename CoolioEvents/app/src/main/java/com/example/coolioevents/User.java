package com.example.coolioevents;

/**
 * Copyright 2025 Avery Dancocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class represents a User
 *
 * RATIONALE:
 * This class was designed to act as a superclass for Organizers and
 * Entrants in order to reduce code redundancy.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-19
 */
public class User {
    private Profile profile;

    /**
     * This method gets the profile of the user
     * @return
     *      Returns the profile of the user
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * This method sets the profile of a user
     * @param profile
     *      the profile to set
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
