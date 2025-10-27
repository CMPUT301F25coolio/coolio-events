package com.example.coolioevents;


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
    public String getEventName() { return eventName; }
    public String getEventDescription() { return eventDescription; }
    public String getRegistrationPeriod() { return registrationPeriod; }
    public int getEntrantLimit() { return entrantLimit; }
    public String getStatus() { return status; }

    // Setters
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    public void setRegistrationPeriod(String registrationPeriod) { this.registrationPeriod = registrationPeriod; }
    public void setEntrantLimit(int entrantLimit) { this.entrantLimit = entrantLimit; }
    public void setStatus(String status) { this.status = status; }
}
