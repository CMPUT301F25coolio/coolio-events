package com.example.coolioevents;

import com.example.coolioevents.organizer.Organizer;

import java.util.ArrayList;
import java.util.List;
/**
 * Copyright 2025 Aasta Tsai
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
 * This class defines an Event.
 * It provides necessary getters and setters for the different attributes
 * of the events.
 * Also holds the different lists for the different categories of entrants
 * for the event; waitlist, chosen, cancelled, and accepted
 *
 * @author Aasta Tsai
 * @version 1.0
 * @since 2025-11-05
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
    private boolean lotteryDone;

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
        this.lotteryDone = true;
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

    /**
     * This method returns if the lottery has been done or not
     * @return isLotteryDone
     */
    public boolean getLotteryDone() {
        return lotteryDone;
    }

    /**
     * This method sets the parameter isLotteryDone
     * @param lotteryDone
     *      a boolean representing if the lottery has been done or not
     */
    public void setLotteryDone(boolean lotteryDone) {
        this.lotteryDone = lotteryDone;
    }

    /**
     * This is the comparison method for Events
     * Events that are posted more recently are deemed higher.
     *
     * @param e
     *      Event to be compared to this event
     * @return
     *      A negative integer, zero, or positive integer
     *      negative int: Indicates this event is lower then event e
     *      zero: Indicates this event is on the same level as event e
     *      positive: Indicates this event is higher then event e
     */
    @Override
    public int compareTo(Event e) {
        if (getClass() == e.getClass()){
            // Events that are posted sooner are sorted higher
            return ((Event) e).getDetails().getPostedDate().compareTo(this.getDetails().getPostedDate());
        }
        return -1;
    }


}
