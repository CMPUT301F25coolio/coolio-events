package com.example.coolioevents.Entrant;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EntrantViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Event>> eventList = new MutableLiveData<ArrayList<Event>>();
    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database

    private CollectionReference usersRef;
    private CollectionReference eventsRef;


    public EntrantViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");


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
}
