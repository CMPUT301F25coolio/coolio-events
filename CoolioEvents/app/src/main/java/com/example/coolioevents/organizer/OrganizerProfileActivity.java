package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.coolioevents.R;
import com.example.coolioevents.UpdateProfileFragment;
import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class represents an activity for the profile of an organizer.
 * It displays the details of an organizer and gives them options to
 * delete their account or logout.
 *
 * RATIONALE:
 * Utilizes firebase information to establish the details or the
 * organizer profile.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-27
 */
public class OrganizerProfileActivity extends AppCompatActivity {
    private TextView profileText;
    private ImageView profileCircle;
    private TextView textUsername;
    private TextView textName;
    private TextView textEmail;
    private Button btnEditProfile;
    private Button logoutButton;
    private FrameLayout btnBack;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_profile);

        // Initialize UI elements
        profileCircle = findViewById(R.id.profile_circle);
        profileText = findViewById(R.id.icon_text);
        textUsername = findViewById(R.id.text_username);
        textName = findViewById(R.id.text_name);
        textEmail = findViewById(R.id.text_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnBack = findViewById(R.id.btnBack);
        logoutButton = findViewById(R.id.logoutButton);

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load user's profile from Firestore
        loadUserProfile();

        // Navigate to the update profile screen when the button is clicked
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, OrganizerUpdateProfileActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            logout(); // If logout button pressed - perform logout
        });

        // Back button -> goes to organizer home
        btnBack.setOnClickListener(v -> {
            finish();
        });


    }

    /**
     * Fetches the logged-in user's profile data from Firestore.
     * If successful, it updates the TextViews with username, name, and email.
     * If the user is not logged in or the document doesn't exist,
     * appropriate error messages are displayed.
     */
    private void loadUserProfile() {
        // Ensure a user is logged in
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the user ID
        String userId = auth.getCurrentUser().getUid();

        // Setting profile color
        int colourId = getColour(userId);
        int userColour = ContextCompat.getColor(this, colourId);
        profileCircle.getBackground().setTint(userColour);

        // Reference the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(userId);

        // Fetch data asynchronously
        userRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String username = document.getString("username");
                        String name = document.getString("name");
                        String email = document.getString("email");

                        if (name != null) {
                            String initials = getInitials(name);
                            profileText.setText(initials);
                        }

                        textUsername.setText((username != null ? username : ""));
                        textName.setText((name != null ? name : ""));
                        textEmail.setText((email != null ? email : ""));
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error fetching profile", e);
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * This method returns a string of initials or a single initial
     * based on the provided string name.
     * @param name
     *      String name from which the initials will be obtained
     */
    private String getInitials(String name) {
        String[] words = name.split(" ");
        int size = words.length;

        if (size == 1) {
            // If there is one whole name just return the first initial
            String firstWord = words[0];
            char firstLetter = firstWord.charAt(0);
            return String.valueOf(firstLetter);
        }
        else if (size >= 2) {
            // Regardless of how many other words are in the name take the first
            // two words and get their first letters
            String firstWord = words[0];
            String secondWord = words[1];
            char firstLetter = firstWord.charAt(0);
            char secondLetter = secondWord.charAt(0);
            return "" + firstLetter + secondLetter;
        }
        return ""; // If no words are found
    }

    /**
     * This function uses hashing to return a colour based on a
     * user's ID. The function will return the same colour
     * for the same user ID every time.
     * @param userId
     *      The user ID that is to be hashed
     * @return
     *      an integer representing a colour
     */
    private int getColour(String userId) {
        int[] colourPalette = new int[]{
                R.color.medium_purple,
                R.color.medium_green,
                R.color.medium_blue,
                R.color.medium_yellow,
        };

        /*
        Taken From: https://docs.vultr.com/java/standard-library/java/lang/String/hashCode
            License: http://www.apache.org/licenses/LICENSE-2.0
            Author: Vultr
            Taken By: Avery Dancocks
            Taken On: 11/26/25
         */
        int hash = userId.hashCode();

        /*
        Taken From: https://stackoverflow.com/questions/33017670/how-to-calculate-an-array-index-from-a-hash
            License:  https://creativecommons.org/licenses/by-sa/3.0/
            Author: Eran
            Taken By: Avery Dancocks
            Taken On: 11/26/25
         */
        int index = Math.abs(hash % colourPalette.length);

        return colourPalette[index];
    }

    /**
     * This method signs the user out of their account - it
     * signs out of the current user in mAuth, and
     * sends user back to the welcome screen
     */
    private void logout(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        auth.signOut();
        startActivity(intent);
        finish();
    }
}
