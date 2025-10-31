package com.example.coolioevents;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.coolioevents.organizer.Organizer;

import java.util.Date;
import java.util.UUID;

public class EventTest {
    public Event makeMockEvent(String eventID, Organizer organizer){
        // Makes a mock event for testing
        String eventName = "Test Event";
        String eventDescription = "This is the description";
        String eventTime = "2025/12/25";
        String eventLocation = "Edmonton";
        String registrationPeriod = "2025/10/25-2025/12/25";
        int entrantLimit = 50;
        String status = "opened";
        Date postedDate = new Date();

        EventDetails testDetails = new EventDetails(eventName, eventDescription, eventTime, eventLocation, registrationPeriod, entrantLimit, status, postedDate);

        return new Event(eventID, organizer.getProfile().getUser_id(), testDetails);
    }

    public Organizer makeMockOrganizer(){
        // Makes a mock organizer for testing
        String name = "Ethan";
        String username = "ethswan";
        String email = "ethan@gmail.com";
        Profile profile = new Profile(UUID.randomUUID().toString(), username, name, email);
        return new Organizer(profile);
    }

    @Test
    public void testgetEventID(){
        //Test getEventID method from event
        String eventID = UUID.randomUUID().toString();
        Organizer organizer = makeMockOrganizer();
        Event event = makeMockEvent(eventID, makeMockOrganizer());
        assert(eventID.equals(event.getEventId()));
    }










}
