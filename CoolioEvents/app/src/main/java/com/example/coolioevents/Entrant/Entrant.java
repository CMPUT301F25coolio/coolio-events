package com.example.coolioevents.Entrant;

import com.example.coolioevents.Profile;
/**
 * This is a class that defines an Entrant
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
