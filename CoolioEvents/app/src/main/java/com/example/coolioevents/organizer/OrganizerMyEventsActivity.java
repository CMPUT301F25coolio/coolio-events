package com.example.coolioevents.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EntrantEventArrayAdapter;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.OrganizerEventArrayAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;

public class OrganizerMyEventsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user; // Current organizer user
    EventViewModel eventViewModel; // View Model eventList up to date with database
    ArrayList<Event> eventsList; // My events-specific arraylist for array adapter
    OrganizerEventArrayAdapter eventAdapter; // Array adapter for events
    ListView eventsListView; // ListView on home fragment screen
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_my_events);


        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventsList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        observeViewModel();

        eventsListView = findViewById(R.id.eventList); // Organizer My Events Listview
        eventAdapter = new OrganizerEventArrayAdapter(this, eventsList); // Makes a new array adapter linked to eventsList
        eventsListView.setAdapter(eventAdapter); // Links eventsListview to eventAdapter
        btnBack = findViewById(R.id.btnBack);

        // Back button sends user back to Organizer Home
        btnBack.setOnClickListener(v -> finish());


        // TODO: event pop up
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    private void observeViewModel(){
        // Sets an an ViewModel observer to check for any event changes in db
        eventViewModel.getEventList().observe(this, new Observer<ArrayList<Event>>() {
            // When eventlist in viewmodel is updated, update eventList too (aswell as notify array adapter)
            @Override
            public void onChanged(ArrayList<Event> events) {
                eventsList.clear();
                System.out.println("CHANGED OMG");
                for (Event event : events){
                    if (event.getOrganizerId().equals(user.getUid())){
                        //Only add events this organizer owns
                        eventsList.add(event);
                    }
                    Collections.sort(eventsList);
                    eventAdapter.notifyDataSetChanged();
                }
            }});
    }




}