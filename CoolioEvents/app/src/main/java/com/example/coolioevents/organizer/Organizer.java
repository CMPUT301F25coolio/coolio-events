package com.example.coolioevents.organizer;

import com.example.coolioevents.Profile;

/**
 * This is a class that defines an Organizer
 */
public class Organizer {
    private Profile profile;


    public Organizer(Profile profile) {
        this.profile = profile;
    }

    /**
     * This method gets the profile of the organizer
     * @return
     *      Returns the profile of the organizer
     */
    public Profile getProfile() {
        return profile;
    }
}
