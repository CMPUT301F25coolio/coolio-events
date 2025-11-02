package com.example.coolioevents.events;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.Event;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventViewModel extends ViewModel {

    //ViewModel needs to hold reference to firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Event>> eventList = new MutableLiveData<>();

    public EventViewModel() {
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

}
