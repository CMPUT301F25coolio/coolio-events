package com.example.coolioevents;

import com.google.firebase.firestore.GeoPoint;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class represents an object that holds a users Id, the event Id and
 * the location of where the user joined the waitlist.
 *
 * RATIONALE:
 * Provides getters and setters for all attributes.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-23
 */
public class WaitlistLocation {
    private String userId;
    private String eventId;
    private GeoPoint location;

    public WaitlistLocation() {} // Empty constructor for firebase

    /**
     * Constructor for a waitlist location
     * @param userId user ID
     * @param eventId event ID
     * @param location location the user joined the event waitlist at
     */
    public WaitlistLocation(String userId, String eventId, GeoPoint location) {
        this.userId = userId;
        this.eventId = eventId;
        this.location = location;
    }

    /**
     * Method to get user ID
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Method to set user ID
     * @param userId
     *      The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Method to get event ID
     * @return eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Method to set event ID
     * @param eventId
     *      The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Method to get location
     * @return location
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * Method to set location of user joining the event waitlist
     * @param location
     *      The location to be set
     */
    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
