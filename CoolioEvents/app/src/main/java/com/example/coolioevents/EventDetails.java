package com.example.coolioevents;

/**
 * This is a class the defines an Event's Details
 */
public class EventDetails {
    private String eventName;
    private String eventDescription;
    private String registrationPeriod;
    private int entrantLimit;
    private String status; // "open" or "closed"

    public EventDetails() {
        // Empty constructor needed for Firebase
    }

    public EventDetails(String eventName, String eventDescription, String registrationPeriod,
                        int entrantLimit, String status) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.registrationPeriod = registrationPeriod;
        this.entrantLimit = entrantLimit;
        this.status = status;
    }

    // Getters

    /**
     * This method gets the name of the event.
     * @return
     *      The name of the event
     */
    public String getEventName() { return eventName; }

    /**
     * This method gets the description of the event.
     * @return
     *      The description of the event
     */
    public String getEventDescription() { return eventDescription; }
    /**
     * This method gets the registration period of the event.
     * @return
     *      The registration period of the event
     */
    public String getRegistrationPeriod() { return registrationPeriod; }

    /**
     * This method gets the entrant limit of the event.
     * @return
     *      The entrant limit of the event
     */
    public int getEntrantLimit() { return entrantLimit; }

    /**
     * This method gets the status of the event.
     * @return
     *      The status of the event
     */
    public String getStatus() { return status; }

    // Setters

    /**
     * This method sets the event's name to eventName
     * @param eventName
     *      The name of the vent
     */
    public void setEventName(String eventName) { this.eventName = eventName; }

    /**
     * This method sets the event's description to eventDescription
     * @param eventDescription
     *      The description of the event
     */
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    /**
     * This method sets the event's registration period to registrationPeriod
     * @param registrationPeriod
     *      The registration period of the vent
     */
    public void setRegistrationPeriod(String registrationPeriod) { this.registrationPeriod = registrationPeriod; }

    /**
     * This method sets the entrant limit of the event to entrantLimit
     * @param entrantLimit
     *      The limit of entrants in the event
     */
    public void setEntrantLimit(int entrantLimit) { this.entrantLimit = entrantLimit; }

    /**
     * This method sets the entrant limit of the event to status
     * @param status
     *      The status of the event
     */
    public void setStatus(String status) { this.status = status; }
}
