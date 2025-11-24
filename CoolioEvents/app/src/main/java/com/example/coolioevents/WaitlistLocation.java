package com.example.coolioevents;

import com.google.firebase.firestore.GeoPoint;

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
