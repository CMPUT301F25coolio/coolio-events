package com.example.coolioevents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * This is a class the defines an Event
 */
public class Event implements Comparable<Event> {

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

    /**
     * This method gets the event's eventID
     * @return
     *      Returns Event's ID
     */
    public String getEventId() { return eventId; }

    /**
     * This method gets the event's details
     * @return
     *      Returns the details of the event
     */
    public EventDetails getDetails() { return details; }

    public List<String> getWaitlistEntrants() { return waitlistEntrants; }
    public List<String> getChosenEntrants() { return chosenEntrants; }
    public List<String> getCancelledEntrants() { return cancelledEntrants; }

    // Setters

    /**
     * This method sets the event's id to eventId
     * @param eventId
     *      The ID the event is set to
     */
    public void setEventId(String eventId) { this.eventId = eventId; }

    /**
     * This method sets the event's details
     * @param details
     *      The details of the event
     */
    public void setDetails(EventDetails details) { this.details = details; }



    @Override
    public int compareTo(Event e) {
        if (getClass() == e.getClass()){
            // Events that are posted sooner are sorted higher
            return ((Event) e).getDetails().getPostedDate().compareTo(this.getDetails().getPostedDate());
        }
        return -1;
    }


}
