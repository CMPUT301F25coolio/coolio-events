package com.example.coolioevents.Entrant;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.Event;
import com.example.coolioevents.Profile;
import com.example.coolioevents.organizer.Organizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntrantViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<ArrayList<Event>>(); // List of all events in db
    private MutableLiveData<Map<String, Organizer>> organizerMap = new MutableLiveData<Map<String, Organizer>>(); //List of all entrants in db
    private MutableLiveData<Map<String, Entrant>> entrantMap = new MutableLiveData<Map<String, Entrant>>(); // List of all entrants in db
    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database

    private CollectionReference usersRef; // Reference to collection "users" in db
    private CollectionReference eventsRef; // Reference to collection "events" in db


    public EntrantViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");

        // Snapshot listener for users in db - updates organizerList and entrantList when updated in db
        usersRef.addSnapshotListener((value, error) ->{
            if (value !=null && !value.isEmpty()){

                organizerMap.setValue(new HashMap<>());
                entrantMap.setValue(new HashMap<>());

                Map<String, Entrant> newEntrantMap = entrantMap.getValue();
                Map<String, Organizer> newOrganizerMap = organizerMap.getValue();

                for (QueryDocumentSnapshot snapshot : value){
                    String userID = snapshot.getId();
                    String username = snapshot.getString("username");
                    String name = snapshot.getString("name");
                    String email = snapshot.getString("email");
                    if (snapshot.getString("role").equals("Organizer")) {
                        // If current user doc's role is organizer, add to organizerlist
                        newOrganizerMap.put(userID, new Organizer(new Profile(userID,username,name,email)));
                    } else if (snapshot.getString("role").equals("Entrant")) {
                        // If current user doc's role is entrant, add to organizerlist
                        newEntrantMap.put(userID, new Entrant(new Profile(userID,username,name,email)));
                    }
                }
                organizerMap.setValue(newOrganizerMap);
                entrantMap.setValue(newEntrantMap);
            }
        });

        // Snapshot listener for events in db - updates eventList when events collection  updated
        eventsRef.addSnapshotListener(((value, error) -> {
            if (value !=null && !value.isEmpty()){

                eventList.setValue(new ArrayList<Event>());
                ArrayList<Event> newlist = eventList.getValue();

                for (QueryDocumentSnapshot snapshot : value){
                    Event newEvent = snapshot.toObject(Event.class);
                    newlist.add(newEvent);
                }
                eventList.setValue(newlist);
            }
        }));



    }

    public MutableLiveData<ArrayList<Event>> getEventList() {
        return eventList;
    }

    public MutableLiveData<Map<String, Organizer>> getOrganizerMap() {
        return organizerMap;
    }

    public MutableLiveData<Map<String, Entrant>> getEntrantMap() {
        return entrantMap;
    }
}
