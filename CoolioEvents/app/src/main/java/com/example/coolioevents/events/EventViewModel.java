package com.example.coolioevents.events;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.Entrant.Entrant;
import com.example.coolioevents.Event;
import com.example.coolioevents.Profile;
import com.example.coolioevents.organizer.Organizer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventViewModel extends ViewModel {

    //ViewModel needs to hold reference to firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<>(); // List of all events in db

    private final MutableLiveData<Map<String, Organizer>> organizerMap = new MutableLiveData<Map<String, Organizer>>(); // List of all organizers in db
    public EventViewModel() {
        addOrganizerSL(); // Makes organizer snapshot listener to keep organizerMap consistent with db
        addEventSL();
    }

    /**
     * This function looks for a specified event in the events documents on firebase.
     * If an event is found it converts the event into an event object and returns it.
     *
     * @param eventId
     *      The event that will be gotten from the firebase and converted into an object
     *      to be returned
     * @return
     *      returns a single event of the type MutableLiveData
     */
    //Getting an event by its ID
    public MutableLiveData<Event> getEventById(String eventId) {
        MutableLiveData<Event> singleEventData = new MutableLiveData<>();

        //Gemini - how to convert firebase document to object oct29
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

    //Gemini - How do i update a list on firestore oct29
    /**
     * Adds a user to an event's waitlist and updates the firebase.
     *
     * @param eventId
     *      event that has the waitlist the user will be added to
     * @param userId
     *      the user that will be added to the event waitlist
     */
    //Joining Waitlist
    public void joinWaitlist(String eventId, String userId) {
        if (eventId == null || userId == null) {
            return;
        }
        db.collection("events").document(eventId)
                .update("waitlistEntrants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                Log.d("ViewModel", "SUCCESS: User " + userId + " added to waitlist for event " + eventId);
                })
                .addOnFailureListener(e -> {
                Log.e("ViewModel", "FAILURE: Could not update waitlist for event " + eventId, e);
            });
    }

    /**
     * Removes a user from an event's waitlist and updates the firebase.
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
        db.collection("events").document(eventId)
                .update("waitlistEntrants", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: User " + userId + " removed from waitlist for event " + eventId);
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not update waitlist for event " + eventId, e);
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
     * with the organizers in the db under "users" collection
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
     * with the events in the db under the "events" collection
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
}
