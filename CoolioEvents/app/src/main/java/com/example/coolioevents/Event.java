package com.example.coolioevents;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String eventId; // optional for Firebase
    private EventDetails details;
    private List<String> waitlistEntrants;
    private List<String> chosenEntrants;
    private List<String> cancelledEntrants;

    public Event() {
        // Empty constructor for Firebase
    }

    public Event(String eventId, EventDetails details) {
        this.eventId = eventId;
        this.details = details;
        this.waitlistEntrants = new ArrayList<>();
        this.chosenEntrants = new ArrayList<>();
        this.cancelledEntrants = new ArrayList<>();
    }

    // Getters
    public String getEventId() { return eventId; }
    public EventDetails getDetails() { return details; }
    public List<String> getWaitlistEntrants() { return waitlistEntrants; }
    public List<String> getChosenEntrants() { return chosenEntrants; }
    public List<String> getCancelledEntrants() { return cancelledEntrants; }

    // Setters
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setDetails(EventDetails details) { this.details = details; }
}
