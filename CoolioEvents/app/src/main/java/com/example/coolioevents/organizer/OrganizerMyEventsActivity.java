package com.example.coolioevents.organizer;

import android.content.Intent;
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
import com.example.coolioevents.MainActivity;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EntrantEventArrayAdapter;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.example.coolioevents.events.OrganizerEventArrayAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Copyright 2025 Ethan Diep & Juliane Phan & Avery Dancocks
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
 * This class represents the Organizer's My Events Activity.
 * It allows the organizer to look at the events they have posted
 * and view more details about them by pressing on them.
 *
 * RATIONALE:
 * Utilizes an event view model to fetch the Organizer's
 * events and display it on a Listview.
 *
 * @author Ethan Diep & Juliane Phan & Avery Dancocks
 * @version 1.0
 * @since 2025-11-06
 */
public class OrganizerMyEventsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user; // Current organizer user
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    EventViewModel eventViewModel; // View Model eventList up to date with database
    ArrayList<Event> eventsList; // My events-specific arraylist for array adapter
    OrganizerEventArrayAdapter eventAdapter; // Array adapter for events
    ListView eventsListView; // ListView on home fragment screen
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_my_events);


        // eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Source - https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
        // Posted by mlykotom
        // Retrieved by Juliane Phan on 2025-11-06, License - CC BY-SA 4.0
        // Used to instantiate the EventViewModel which uses the EventViewModel Factory class
        // Modifications made: Used our own class and parameter names
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

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
                Event event = eventsList.get(position);
                // Create Intent to start OrganizerEventActivity
                Intent intent = new Intent(OrganizerMyEventsActivity.this, OrganizerEventActivity.class);
                // Put event ID into the intent
                System.out.println(event.getEventId());
                intent.putExtra("EVENT_ID", event.getEventId());
                // Start the OrganizerEventActivity
                startActivity(intent);
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