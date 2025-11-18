package com.example.coolioevents.Entrant;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.example.coolioevents.NotificationFragment;
import com.example.coolioevents.ProfileFragment;
import com.example.coolioevents.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
/**
 * Copyright 2025 Ethan Diep, Niharika Rawat
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
 * This class represents the Entrant Activity which is an activity with
 * a frame container which holds entrant fragment screens such as home, search, profile
 * and my events aswell as a navigation bar to navigate between these screens.
 *
 * RATIONALE:
 * This class was designed give entrant the ability to navigate through
 * the different fragments they have access to
 *
 * @author Ethan Diep, Niharika Rawat
 * @version 1.0
 * @since 2025-11-06
 */
public class EntrantActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; // Authenticator to create user accounts
    private FirebaseFirestore db; // Database
    private CollectionReference userCollection; // Collection of users in firebase database

    private FirebaseUser user; // The current user
    private Fragment homeFragment; // Fragment for home screen
    private Fragment myEventsFragment; // Fragment for home screen
    private Fragment ProfileFragment;
    private BottomNavigationView bottomNavView;



    private Map<String, Entrant> entrantMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant);
        homeFragment = new EntrantHomeFragment();
        myEventsFragment = new EntrantMyEventsFragment();
        ProfileFragment = new ProfileFragment();

        SwitchFragment(homeFragment); // Default fragment is home

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        user = mAuth.getCurrentUser();
        bottomNavView = findViewById(R.id.bottomNavigationView);
        bottomNavView.setItemIconTintList(null);
        SwitchFragment(homeFragment);

        bottomNavView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.home){
                // If home item is selected in nav bar switch to home fragment
                SwitchFragment(homeFragment);
            } else if (menuItem.getItemId() == R.id.myevents) {
                // If myevents item is selected in nav bar switch to home fragment
                SwitchFragment(myEventsFragment);
            } else if (menuItem.getItemId() == R.id.profile) {
                // If myevents item is selected in nav bar switch to home fragment
                SwitchFragment(ProfileFragment);
            }
//           else if (menuItem.getItemId() == R.id.search) {
//                // TODO
//                SwitchFragment(SearchEvent);
//            }
            return true;
        });
    }
    // 1. Make the Bell Icon appear
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // 2. Handle what happens when the Bell is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {

            // Create the notification fragment
            NotificationFragment notificationFragment = new NotificationFragment();

            // We manually do the transaction here because we want 'addToBackStack'
            // This allows the user to press the "Back" button on their phone to return to Home
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, notificationFragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Switches fragment to fragment in the fragment container
     * @param fragment The fragment to switch to
     */
    public void SwitchFragment(Fragment fragment){
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.fragment_container, fragment);
        fragTransaction.commit();
    }
}