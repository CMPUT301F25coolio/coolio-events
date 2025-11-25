package com.example.coolioevents.events;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.Entrant.Entrant;
import com.example.coolioevents.Event;
import com.example.coolioevents.Profile;
import com.example.coolioevents.User;
import com.example.coolioevents.WaitlistLocation;
import com.example.coolioevents.organizer.Organizer;
import com.example.coolioevents.services.LotteryResult;
import com.example.coolioevents.services.LotteryService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Copyright 2025 Avery Dancocks & Juliane Phan & Ethan Diep
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class represents an event viewmodel, responsible for holding information
 * related to events from the db, and contains methods that allow for the interaction
 * with the events in the database. It contains an eventList containing all events in the db,
 * kept up to date with it. It also has an organizerMap containing all organizers,
 * also kept up to date in the db.
 *
 *
 * RATIONALE:
 * This class was designed to allow users to browse events they may be
 * interested in.
 *
 * @author Avery Dancocks & Juliane Phan & Ethan Diep
 * @version 1.5
 * @since 2025-11-06
 */
public class EventViewModel extends ViewModel {

    // ViewModel needs to hold reference to firebase
    private final FirebaseFirestore db;
    private final MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<>(); // List of all events in db
    private final MutableLiveData<ArrayList<User>> organizerList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<User>> entrantList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<WaitlistLocation>> locationsList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<EventImageData>> eventImageList = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Organizer>> organizerMap = new MutableLiveData<Map<String, Organizer>>(); // Map of all organizers in db (key: userid, value: Organizer object)

    // Make a LotteryService Object
    private final LotteryService lotteryService = new LotteryService();
    public EventViewModel(FirebaseFirestore db) {
        this.db = db;
        addOrganizerSL(); // Makes organizer snapshot listener to keep organizerMap consistent with db
        addEventSL(); // Makes event snapshot listener to keep eventList consistent with db
    }

    /**
     * This function looks for a specified event in the events documents on firebase.
     * If an event is found it converts the event into an event object and returns it.
     *
     * @param eventId
     *      The event that will be retrieved from the firebase and converted into an object
     *      to be returned
     * @return
     *      returns a single event of the type MutableLiveData
     */
    public MutableLiveData<Event> getEventById(String eventId) {
        MutableLiveData<Event> singleEventData = new MutableLiveData<>();

        /*Taken from: Google Gemini
                Prompt: how to convert firebase document to object?
                Taken by: Avery Dancocks
                Taken on: 10/29/25
         */
        //Get document from firebase and convert to event object
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) { //make sure the event actually exists
                        event.setEventId(documentSnapshot.getId());
                    }
                    singleEventData.postValue(event);
                });
        return singleEventData;
    }

    /**
     * This function looks for a specified organizer in the users documents on firebase.
     * If an organizer is found it converts the event into an event object and returns it,
     * with its matching profile.
     * @param organizerId
     *      The Organizer that will be retrieved from the firebase and converted into an object
     *      to be returned
     * @return
     *      returns a single organizer of the type MutableLiveData
     */
    public MutableLiveData<Organizer> getOrganizerById(String organizerId) {
        MutableLiveData<Organizer> singleOrganizerData = new MutableLiveData<>();


        db.collection("users").document(organizerId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Organizer organizer = documentSnapshot.toObject(Organizer.class);
                    if (organizer != null) {
                        Profile profile = new Profile();
                        // Set all profile aspects
                        profile.setUserId(documentSnapshot.getId());

                        String username = documentSnapshot.getString("username");
                        profile.setUsername(username);

                        String name = documentSnapshot.getString("name");
                        profile.setName(name);

                        String email = documentSnapshot.getString("email");
                        profile.setEmail(email);

                        organizer.setProfile(profile);
                    }
                    singleOrganizerData.setValue(organizer);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "Error getting the organizer", e);
                    singleOrganizerData.setValue(null);
                });
        return singleOrganizerData;
    }

    /**
     * This function returns a LiveData ArrayList of WaitlistLocation objects
     * that have the eventId matching the eventId parameter passed.
     *
     * @param eventId
     *      The event ID to match in the WaitlistLocation objects
     * @return
     *      A MutableLiveData ArrayList of WaitlistLocations
     */
    public MutableLiveData<ArrayList<WaitlistLocation>> getWaitlistLocations(String eventId) {
        ArrayList<WaitlistLocation> locations = new ArrayList<>();

        db.collection("waitlist_locations").whereEqualTo("eventId", eventId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        WaitlistLocation waitlistLocation = documentSnapshot.toObject(WaitlistLocation.class);
                        locations.add(waitlistLocation);
                    }
                    locationsList.postValue(locations);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "Error getting waitlist locations", e);
                    locationsList.postValue(null);
                });
        return locationsList;
    }

    /**
     * This function returns a LiveData ArrayList of Users that may be Organizers
     * or Entrants from firebase depending on the "role" parameter provided.
     * @param role
     *      A string representing either "Organizer" or "Entrant"
     * @return
     *      A MutableLiveData ArrayList of Users where Users can be either
     *      Organizers or Entrants depending on the provided "role" argument
     */
    /*Taken from: Google Gemini
        Prompt: Best way to return a list of objects from a viewmodel in firebase?
        Taken by: Avery Dancocks
        Taken on: 11/19/25
     */
    public MutableLiveData<ArrayList<User>> getUserList(String role) {
        ArrayList<User> users = new ArrayList<>();

        db.collection("users").whereEqualTo("role", role).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            Profile profile = new Profile();
                            // Set all profile aspects
                            profile.setUserId(documentSnapshot.getId());

                            String username = documentSnapshot.getString("username");
                            profile.setUsername(username);

                            String name = documentSnapshot.getString("name");
                            profile.setName(name);

                            String email = documentSnapshot.getString("email");
                            profile.setEmail(email);

                            user.setProfile(profile);
                            users.add(user);
                        }
                    }
                    if (role.equals("Organizer")) {
                        organizerList.postValue(users);
                    }
                    if (role.equals("Entrant")) {
                        entrantList.postValue(users);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "Error fetching organizers", e);
                    if (role.equals("Organizer")) {
                        organizerList.postValue(null);
                    }
                    if (role.equals("Entrant")) {
                        entrantList.postValue(null);
                    }
                });

        if (role.equals("Organizer")) {
            return organizerList;
        }
        if (role.equals("Entrant")) {
            return entrantList;
        }
        return null;
    }

    /**
     * This function returns a LiveData ArrayList of EventImageData that
     * contains the events image URL, and the organizer of the event.
     * @return
     *      MutableLiveData ArrayList of EventImageData
     */
    public MutableLiveData<ArrayList<EventImageData>> getEventImages() {
        ArrayList<EventImageData> eventImageData= new ArrayList<>();
        List<Task<DocumentSnapshot>> usernameLookupTasks = new ArrayList<>();

        db.collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshot) {
                        String imageUrl = documentSnapshot.getString("posterUrl");

                        Event event = documentSnapshot.toObject(Event.class);
                        EventImageData newEventImage = new EventImageData();

                        // Getting organizer ID and Image URL
                        String organizerId = event.getOrganizerId();
                        String imageURL = event.getDetails().getPosterUrl();

                        // Setting organizer ID and Image URL
                        newEventImage.setOrganizerId(organizerId);
                        newEventImage.setEventPoster(imageURL);

                        eventImageData.add(newEventImage);

                        /*Taken from: Google Gemini
                            Prompt: How do i get username from user Id to prevent firestore retrieval delay?
                            Taken by: Avery Dancocks
                            Taken on: 11/24/25
                         */
                        // Do a task to look up the username
                        Task<DocumentSnapshot> userTask = db.collection("users").document(organizerId).get();
                        usernameLookupTasks.add(userTask);
                    }

                    // Get usernames of all organizers
                    Tasks.whenAllSuccess(usernameLookupTasks).addOnSuccessListener(userSnapshots -> {
                        for (int i = 0; i < userSnapshots.size(); i++) { // Go through all the users
                            DocumentSnapshot userDoc = (DocumentSnapshot) userSnapshots.get(i);
                            EventImageData eventData = eventImageData.get(i); // Getting matching event data

                            if (userDoc != null) {
                                String username = userDoc.getString("username"); // Get username
                                if (username != null) {
                                    eventData.setOrganizerUsername(username); // Set username
                                }
                                else {
                                    eventData.setOrganizerUsername("Unknown User");
                                }
                            }
                        }
                     eventImageList.postValue(eventImageData);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "Error fetching event images", e);
                    eventImageList.postValue(null);
                });

        return eventImageList;
    }

    /*Taken from: Google Gemini
        Prompt: How do i update a list on firestore?
        Taken by: Avery Dancocks
        Taken on: 10/29/25
     */
    /**
     * Adds a user to an event's waitlist and updates the firebase.
     * Also creates a waitlist location document if the location parameter
     * passed is not null.
     *
     * @param eventId
     *      event that has the waitlist the user will be added to
     * @param userId
     *      the user that will be added to the event waitlist
     * @param location
     *      the GeoPoint location that the user is joining the waitlist from
     */
    public void joinWaitlist(String eventId, String userId, @Nullable GeoPoint location) {
        if (eventId == null || userId == null) {
            return;
        }

        /* Taken From: https://firebase.google.com/docs/firestore/manage-data/transactions#java_4
            License: http://www.apache.org/licenses/LICENSE-2.0
            Authored by: Firebase
            Taken by: Avery Dancocks
            Taken on: 11/24/25
         */
        WriteBatch batch = db.batch();

        // Update Waitlist - always occurs
        DocumentReference eventRef = db.collection("events").document(eventId);
        batch.update(eventRef, "waitlistEntrants", FieldValue.arrayUnion(userId));

        // Save Location - conditional
        if (location != null) {// If location was provided
            WaitlistLocation waitlistLocation = new WaitlistLocation(userId, eventId, location);

            // Make a new location document
            DocumentReference locationRef = db.collection("waitlist_locations").document();

            // Set the new location in the batch
            batch.set(locationRef, waitlistLocation);
        }

        // Commiting the batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: User " + userId + " added to waitlist for event " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not update waitlist for event " + eventId, e);
                });
       
    }

    /**
     * Removes a user from an event's waitlist and updates the firebase.
     * Also removes their waitlist location document if it exists.
     *
     * @param eventId
     *      event that has the waitlist the user will be removed from
     * @param userId
     *       the user that will be removed from the event waitlist
     */
    public void leaveWaitlist(String eventId, String userId) {
        if (eventId == null || userId == null) {
            return;
        }

        // Update Waitlist - always occurs
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.update("waitlistEntrants", FieldValue.arrayRemove(userId));

        // Remove waitlist location - if present
        db.collection("waitlist_locations")
                .whereEqualTo("eventId", eventId).whereEqualTo("userId", userId)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) { // A document was found
                        DocumentReference locationRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        locationRef.delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ViewModel", "SUCCESS: User location was removed");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ViewModel", "FAILURE: Found, but could not delete location", e);
                            });
                    }
                    else { // No document to delete
                        Log.d("ViewModel", "No location document, nothing to delete");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not look for location document", e);
                });

    }

    /**
     * This is called when a user chooses to accept the invite for an event.
     * Removes a user from an event's list of chosen entrants,
     * adds a user to an event's list of accepted entrants,
     * and updates the firebase.
     *
     * @param eventId
     *      event that the user is accepting the invite for
     * @param userId
     *       the user that accepts the invite
     */
    public void acceptInvite(String eventId, String userId) {
        if (eventId == null || userId == null) {
            return;
        }

        db.collection("events").document(eventId)
                .update(
                        "chosenEntrants", FieldValue.arrayRemove(userId),
                        "acceptedEntrants", FieldValue.arrayUnion(userId)
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: User " + userId + " removed from chosenEntrants and added to acceptedEntrants for event " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not update chosenEntrants and acceptedEntrants for event " + eventId, e);
                });
    }

    /**
     * This is called when a user chooses to decline the invite for an event.
     * Removes a user from an event's list of chosen entrants,
     * adds a user to an event's list of cancelled entrants,
     * and updates the firebase.
     *
     * @param eventId
     *      event that the user is declining the invite for
     * @param userId
     *       the user that declines the invite
     */
    public void declineInvite(String eventId, String userId) {
        if (eventId == null || userId == null) {
            return;
        }

        db.collection("events").document(eventId)
                .update(
                        "chosenEntrants", FieldValue.arrayRemove(userId),
                        "cancelledEntrants", FieldValue.arrayUnion(userId)
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: User " + userId + " removed from chosenEntrants and added to cancelledEntrants for event " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not update chosenEntrants and cancelledEntrants for event " + eventId, e);
                });
    }

    /**
     * This is called when a user chooses to unregister from an event.
     * Removes a user from an event's list of accepted entrants and updates the firebase.
     *
     * @param eventId
     *      event that the user is unregistering from
     * @param userId
     *       the user that unregisters from the event
     */
    public void unregisterFromEvent(String eventId, String userId) {
        if (eventId == null || userId == null) {
            return;
        }

        db.collection("events").document(eventId)
                .update("acceptedEntrants", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: User " + userId + " removed from acceptedEntrants for event " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not update acceptedEntrants for event " + eventId, e);
                });
    }
    /**
     * Establishes an organizer snapshot listener which updates organizerMap to stay up to date
     * with the organizers in the db under "users" collection. Makes organizers in db as a map of
     * organizer objects
     */
    private void addOrganizerSL(){
        // Snapshot listener for users in db - updates organizerMap when updated in db
        db.collection("users").addSnapshotListener((value, error) ->{
            if (value !=null && !value.isEmpty()){
                organizerMap.setValue(new HashMap<>()); // Make organizerMap empty
                Map<String, Organizer> newOrganizerMap = organizerMap.getValue(); // Placeholder organizerMap which will be assigned to organizerMap later
                for (QueryDocumentSnapshot snapshot : value){
                    String userID = snapshot.getId();
                    String username = snapshot.getString("username");
                    String name = snapshot.getString("name");
                    String email = snapshot.getString("email");
                    if (snapshot.getString("role").equals("Organizer")) {
                        // If current user doc's role is organizer, add to organizerMap
                        newOrganizerMap.put(userID, new Organizer(new Profile(userID,username,name,email)));
                    }
                }
                organizerMap.setValue(newOrganizerMap); // Sets organizerMap to updated organizerMap
            }
        });
    }
    /**
     * Establishes an event snapshot listener which updates eventList to stay up to date
     * with the events in the db under the "events" collection. Make events in db as a list of
     * event objects.
     */
    private void addEventSL(){
        // Snapshot listener for events in db - updates eventList when events collection  updated
        db.collection("events").addSnapshotListener(((value, error) -> {
            if (value !=null && !value.isEmpty()){
                eventList.setValue(new ArrayList<Event>()); // Make eventList Empty
                ArrayList<Event> newlist = eventList.getValue(); // Placeholder eventList which will be assigned to eventList later
                for (QueryDocumentSnapshot snapshot : value){
                    Event newEvent = snapshot.toObject(Event.class);
                    newEvent.setOrganizer(organizerMap.getValue().get(newEvent.getOrganizerId()));
                    newlist.add(newEvent);
                }
                eventList.setValue(newlist); // Sets eventList to updated eventList
            }
        }));
    }
    /**
     * Gets eventList as a MutableLiveData type containing an ArrayList of events.
     * @return
     * eventList as a MutableLiveData type
     */
    public MutableLiveData<ArrayList<Event>> getEventList() {
        return eventList;
    }

    /**
     * Gets the filtered eventList based on some parameters
     * @param startDate
     * Start date of range to filter events
     * @param endDate
     * End date of range ot filter events
     * @param tags
     * Tags to filter events
     *
     * @return
     * An eventlist which is filtered according to the parameters
     */
    public ArrayList<Event> getFilteredEventList(Date startDate, Date endDate, ArrayList<String> tags) {
        ArrayList<Event> events = eventList.getValue(); // Unfiltered events
        ArrayList<Event> filteredEventList = new ArrayList<>(); // Filtered events to be returned

        if (startDate == null || endDate == null){
            // If startDate or endDate is null don't apply date range filter (let range be the origin of time to the end of time)
            startDate = new Date(0);
            endDate = new Date(10000000000000L);
        }

        // Apply add events which conform to filters
        for (Event event : events) {
            Date eventTime = event.getDetails().getEventDateTime();
                // Filter -- Date range and Tags
                if (eventTime.after(startDate) && eventTime.before(endDate)
                    && event.getDetails().getTags().stream().anyMatch(tags::contains)) {
                    filteredEventList.add(event);
            }
        }
        return filteredEventList;
    }
    /**
     * This function runs a lottery for a given event and updates firebase
     * @param event event to run the lottery for
     */
    public void runLottery(Event event) {
        // If event does not exist or if the lottery for an event has already been run
        if (event == null || event.getLotteryDone()) {
            Log.d("ViewModel", "Lottery can not be run; event is either null, or lottery has already been run.");
            return;
        }

        // Get lottery results using the lottery service
        List<String> waitlist = event.getWaitlistEntrants();
        int entrantLimit = event.getDetails().getEntrantLimit();

        LotteryResult result = lotteryService.selectEntrants(waitlist, entrantLimit);

        // Get event ID
        String eventId = event.getEventId();

        // Update firebase
        db.collection("events").document(eventId)
                .update("chosenEntrants", result.getSelectedEntrants(),
                        "waitlistEntrants", result.getRemainingWaitlist(),
                        "lotteryDone", true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: lottery run and database updated for event:" + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not update acceptedEntrants for event " + eventId, e);
                });
    }
}
