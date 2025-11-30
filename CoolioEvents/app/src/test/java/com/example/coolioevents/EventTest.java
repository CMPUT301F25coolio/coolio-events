package com.example.coolioevents;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.coolioevents.organizer.Organizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

public class EventTest {
    public Event makeOpenMockEvent(String eventID, Organizer organizer){
        // Makes a mock event for testing which is open (Today is within registration period time)
        String eventName = "Test Event";
        String eventDescription = "This is the description";
        Date eventTime = new GregorianCalendar(2099, 12, 26).getTime();
        String eventLocation = "Edmonton";
        String registrationPeriod = "2025/10/25-2099/12/25"; // In registration period
        int entrantLimit = 50;
        Date postedDate = new Date();

        EventDetails testDetails = new EventDetails(eventName, eventDescription, registrationPeriod, entrantLimit, eventTime, eventLocation, postedDate);

        return new Event(eventID, organizer.getProfile().getUser_id(), testDetails);
    }

    public Event makeClosedMockEvent(String eventID, Organizer organizer){
        // Makes a mock event for testing which is open (Today is within registration period time)
        String eventName = "Test Event";
        String eventDescription = "This is the description";
        Date eventTime = new GregorianCalendar(2010, 12, 21).getTime();
        String eventLocation = "Edmonton";
        String registrationPeriod = "2010/10/25-2010/12/20"; // Out of registration period
        int entrantLimit = 50;
        Date postedDate = new Date();

        EventDetails testDetails = new EventDetails(eventName, eventDescription, registrationPeriod, entrantLimit, eventTime, eventLocation, postedDate);

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
    public void testParseRegistrationPeriod(){
        //Tests start and end date parser in Event class
        String eventID = UUID.randomUUID().toString(); // Generate random Organizer ID
        Organizer organizer = makeMockOrganizer(); // Make Mock Organizer
        Event event = makeOpenMockEvent(eventID, makeMockOrganizer()); // Make Mock Event
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date startDate;
        Date endDate;

        // Parses known start and endDate for events
        try {
            startDate = sdf.parse("2025/10/25");
            endDate = sdf.parse("2099/12/25");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Tests to see if the event parses dates successfully
        assert(event.getDetails().getStartDate().equals(startDate));
        assert(event.getDetails().getEndDate().equals(endDate));
    }

    @Test
    public void testUpdateStatus(){
        //Tests to see if status is updated when events are constructed (If in (opened) or out (closed) of registration period)
        String eventID = UUID.randomUUID().toString(); // Generate random Organizer ID
        Organizer organizer = makeMockOrganizer(); // Make Mock Organizer
        Event openEvent = makeOpenMockEvent(eventID, makeMockOrganizer()); // Make Mock Event
        Event closedEvent = makeClosedMockEvent(eventID, makeMockOrganizer()); // Make Mock Event
        assert(openEvent.getDetails().getStatus().equals("open")); //Closed event should be closed
        assert(closedEvent.getDetails().getStatus().equals("closed")); //Open event should be open
    }

    @Test
    public void testSetRegistrationDate(){
        //Tests setRegistrationDate to see if it changes registrationDate but also startDate, endDate, and status
        String eventID = UUID.randomUUID().toString(); // Generate random Organizer ID
        Organizer organizer = makeMockOrganizer(); // Make Mock Organizer
        Event openEvent = makeOpenMockEvent(eventID, makeMockOrganizer()); // Make Mock Event
        Event closedEvent = makeOpenMockEvent(eventID, makeMockOrganizer()); // Make Mock Event

        String closedRegPeriod = "2010/10/25-2010/12/20"; // Registration period which makes event closed
        String openRegPeriod = "2025/10/25-2099/12/25"; // Registration period which makes event open

        openEvent.getDetails().setRegistrationPeriod(closedRegPeriod); // change registrationPeriod of open Event so its closed now
        closedEvent.getDetails().setRegistrationPeriod(openRegPeriod); // change registrationPeriod of closed Event so its open now

        //Make sure new registration periods return right string
        assert(openEvent.getDetails().getRegistrationPeriod().equals(closedRegPeriod));
        assert(closedEvent.getDetails().getRegistrationPeriod().equals(openRegPeriod));

        //Tests to see if status of each event are correct
        assert(openEvent.getDetails().getStatus().equals("closed")); //openEvent should be closed
        assert(closedEvent.getDetails().getStatus().equals("open")); //closedEvent should be open

        //Tests to see if new startDate and endDate in events are parsed correctly
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date openEventStartDate;
        Date openEventEndDate;
        Date closedEventStartDate;
        Date closedEventEndDate;
        try {
            openEventStartDate = sdf.parse("2010/10/25");
            openEventEndDate = sdf.parse("2010/12/20");
            closedEventStartDate = sdf.parse("2025/10/25");
            closedEventEndDate = sdf.parse("2099/12/25");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        assert(openEvent.getDetails().getStartDate().equals(openEventStartDate));
        assert(openEvent.getDetails().getEndDate().equals(openEventEndDate));
        assert(closedEvent.getDetails().getStartDate().equals(closedEventStartDate));
        assert(closedEvent.getDetails().getEndDate().equals(closedEventEndDate));
    }









}
