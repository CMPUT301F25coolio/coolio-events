package com.example.coolioevents.organizer;

import com.example.coolioevents.Profile;

/**
 * This is a class that defines an Organizer
 */
public class Organizer {
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
