package com.example.coolioevents.events;

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
 * This class represents an Image as well as the Dd and username
 * of the organizer who posed it.
 *
 * RATIONALE:
 * This class was designed to store information that can be accessed by
 * administrators when browsing through images.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-27
 */
public class EventImageData {
    private String eventPoster;
    private String organizerUsername;
    private String organizerId;

    /**
     * Constructor to make the object
     */
    public EventImageData() {}

    /**
     * This function returns the string URL of the event Poster
     * @return
     *      String URL
     */
    public String getEventPoster() {
        return eventPoster;
    }

    /**
     * This function returns the organizer's username
     * @return
     *      String of username
     */
    public String getOrganizerUsername() {
        return organizerUsername;
    }

    /**
     * This function returns the organizer's ID
     * @return
     *      String of ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * This function sets the poster URL string
     * @param eventPoster
     *      String URL to set
     */
    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    /**
     * This function sets the organizers username
     * @param organizerUsername
     *      The username to set
     */
    public void setOrganizerUsername(String organizerUsername) {
        this.organizerUsername = organizerUsername;
    }

    /**
     * This function sets the organzier's ID
     * @param organizerId
     *      ID to set
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
}


