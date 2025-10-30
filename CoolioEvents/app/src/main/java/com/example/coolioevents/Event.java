package com.example.coolioevents;

import com.example.coolioevents.Entrant.Entrant;
import com.example.coolioevents.organizer.Organizer;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * This is a class the defines an Event
 */
public class Event implements Comparable<Event> {

    private String eventId; // optional for Firebase
    private Organizer organizer;
    private String organizerId;
    private EventDetails details;
    private List<String> waitlistEntrants;  // Entrants who are in the waitlist
    private List<String> chosenEntrants;  // Entrants who were chosen from the waitlist
    private List<String> cancelledEntrants;  // Entrants who cancelled their invite
    private List<String> acceptedEntrants;  // Entrants who accepted their invite

    public Event() {
        // Empty constructor for Firebase
    }


    public Event(String eventId, String organizerId, EventDetails details) {
        this.eventId = eventId;
        this.organizerId = organizerId;
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
     * This method gets the event's organizer object
     * @return
     *      Returns Event's organizer object
     */
    public Organizer getOrganizer() {return organizer;}

    /**
     * This method gets the event's organizer's userID
     * @return
     *      Returns Event's organizer's userID
     */
    public String getOrganizerId() { return organizerId; }

    /**
     * This method gets the event's details
     * @return
     *      Returns the details of the event
     */
    public EventDetails getDetails() { return details; }

    /**
     * This method gets the Entrants in the event's Waitlist
     * @return
     *      Returns the entrants in the waitlist of the event
     */
    public List<String> getWaitlistEntrants() { return waitlistEntrants; }

    /**
     * This method gets the Entrants that were chosen for the event
     * @return
     *      Returns the entrants that were chosen for the event
     */
    public List<String> getChosenEntrants() { return chosenEntrants; }

    /**
     * This method gets the Entrants that were cancelled from the event
     * @return
     *      Returns the entrants that were cancelled from the event
     */
    public List<String> getCancelledEntrants() { return cancelledEntrants; }

    /**
     * This method gets the Entrants that accepted their invite
     * @return
     *      Returns the entrants that accepted their invite
     */
    public List<String> getAcceptedEntrants() { return acceptedEntrants; }

    // Setters

    /**
     * This method sets the event's id to eventId
     * @param eventId
     *      The ID the event is set to
     */
    public void setEventId(String eventId) { this.eventId = eventId; }
    /**
     * This method sets the event's organizer object to organizer
     * @param organizer
     *      The event's organizer object
     */
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    /**
     * This method sets the event's details
     * @param details
     *      The details of the event
     */
    public void setDetails(EventDetails details) { this.details = details; }

    //!!!!Add description
    @Override
    public int compareTo(Event e) {
        if (getClass() == e.getClass()){
            // Events that are posted sooner are sorted higher
            return ((Event) e).getDetails().getPostedDate().compareTo(this.getDetails().getPostedDate());
        }
        return -1;
    }

}
