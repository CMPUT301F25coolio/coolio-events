package com.example.coolioevents.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.R;
import com.example.coolioevents.User;
import com.example.coolioevents.events.EventViewModel;
import com.example.coolioevents.events.EventViewModelFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Copyright 2025 Avery Dancocks & Juliane Phan
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
 * This class represents the administrator entrants screen.
 * It displays all of the entrants that are using the app.
 * Administrators can click on different entrants to interact with
 * their profile.
 *
 * RATIONALE:
 * This class was designed to allow administrators to view and
 * interact with all possible entrants.
 *
 * @author Avery Dancocks & Juliane Phan
 * @version 1.0
 * @since 2025-11-19
 */

public class AdministratorEntrantsActivity extends AppCompatActivity {
    EventViewModel eventViewModel; // View Model eventList up to date with database
    ArrayList<User> entrantsList; // My Entrant specific arraylist for array adapter ()
    UserArrayAdapter profileAdapter; // Array adapter for organizer
    ListView entrantListView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrator_entrants);

        // Establishing views
        ImageButton backButton = findViewById(R.id.btnBack);
        entrantListView = findViewById(R.id.entrant_list_view);

        // Establishing Adapter
        entrantsList = new ArrayList<User>();
        profileAdapter = new UserArrayAdapter(this, entrantsList);
        entrantListView.setAdapter(profileAdapter);

        // Establish ViewModel
        eventViewModel = new ViewModelProvider(this, new EventViewModelFactory(db)).get(EventViewModel.class);

        eventViewModel.getUserList("Entrant").observe(this, new Observer<ArrayList<User>>() {
            // When entrant list in viewmodel is updated, update entrantList too (as well as notify array adapter)
            @Override
            public void onChanged(ArrayList<User> entrants) {
                entrantsList.clear();
                System.out.println("CHANGED OMG");
                entrantsList.addAll(entrants);
                profileAdapter.notifyDataSetChanged();
            }
        });

        // Click specific entrant --> Show fragment with user details and delete button
        entrantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User clickedEntrant = (User) parent.getItemAtPosition(position);

                // If organizer is null do nothing
                if (clickedEntrant == null) {
                    return;
                }

                // Set the fragment's background colour
                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                fragmentContainer.setBackgroundResource(R.drawable.whitebackground);

                AdministratorUserFragment userFragment = AdministratorUserFragment.newInstance(clickedEntrant.getProfile().getUser_id());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, userFragment) // Replace the current fragment
                        .addToBackStack(null) // This allows the user to press the back button to return to the list
                        .commit();
            }
        });

        // Back button onclick activity --> Leads to Home activity
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorHomeActivity.class)));
        }
    }

}