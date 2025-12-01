package com.example.coolioevents;

/**
 * Copyright 2025 Niharika Rawat, Avery Dancocks
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
 * This fragment provides a simple interface for editing and updating a user's
 * profile information (username, name, and email). It can be connected to Firebase,
 * SQLite, or other storage systems to persist profile updates.
 *
 * Once the profile is saved, the fragment navigates back to the previous screen.
 *
 * @author Niharika Rawat, Avery Dancocks
 * @version 1.5
 * @since 2025-11-07
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment that allows users to edit their profile details.
 * This includes username, name, and email.
 * The data can later be saved to a database or cloud service such as Firebase.
 */
public class UpdateProfileFragment extends Fragment {

    // TextView displaying the user's profile
    private TextView profileText;

    // ImageView for profile circle
    private ImageView profileCircle;

    // Input field for editing the user's username
    private EditText editUsername;

    // Input field for editing the user's full name
    private EditText editName;

    // Input field for editing the user's email address
    private EditText editEmail;

    // Button to save the updated profile information
    private Button btnSave;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Back Button to go back instead of saving
    private Button btnBack;


    /**
     * Called to create and return the view hierarchy associated with the fragment.
     * This method inflates the fragment layout, initializes input fields,
     * and sets up the save button logic.
     *
     * @param inflater LayoutInflater used to inflate the fragment layout
     * @param container Optional parent view that the fragment UI attaches to
     * @param savedInstanceState Previously saved state, if any
     * @return The created View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        // Initialize input fields and button
        profileCircle = view.findViewById(R.id.update_profile_circle);
        profileText = view.findViewById(R.id.icon_text);
        editUsername = view.findViewById(R.id.edit_username);
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnBack = view.findViewById(R.id.btn_back_profile);

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadEditText();

        /**
         * Handles the save button click event.
         * Retrieves input field values, displays a confirmation toast,
         * and navigates back to the previous fragment.
         */
        btnSave.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            updateUserprofile(username, name, email);

            // Navigate back to the previous fragment (ProfileFragment)
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    /**
     * Fetches the logged-in user's profile data from Firestore.
     * If successful, it updates the EditViews with username, name, and email.
     * If the user is not logged in or the document doesn't exist,
     * appropriate error messages are displayed.
     */
    private void loadEditText() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the user ID
        String userId = auth.getCurrentUser().getUid();

        // Setting profile color
        int colourId = getColour(userId);
        int userColour = ContextCompat.getColor(getContext(), colourId);
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

                        editUsername.setText((username != null ? username : ""));
                        editName.setText((name != null ? name : ""));
                        editEmail.setText((email != null ? email : ""));
                    } else {
                        Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error fetching profile", e);
                    Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                });
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
     *This function updates the newly inputed name, username, and email
     * for a given user in firebase.
     * @param username
     *      username to be updated
     * @param name
     *      name to be updated
     * @param email
     *      email to be updated
     */
    public void updateUserprofile(String username, String name, String email) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the user ID
        String userId = auth.getCurrentUser().getUid();

        // Reference the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(userId);

        // Creating map of the data to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("name", name);
        updates.put("email", email);

        // Actually do the update on firestore
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    /*
                    Taken From: Google Gemini
                        Prompt: why am I getting a NullPointerException?
                        Taken By: Avery Dancocks
                        Taken On: 11/26/25
                     */
                    if (getContext() == null || !isAdded()) {
                        return; // Prevent crash if fragment is detached
                    }
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    // Go back to profile
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener((e -> {
                    if (getContext() == null || !isAdded()) {
                        return; // Prevent crash if fragment is detached
                    }

                    Log.e("UpdateProfileFragment", "Error updating profile", e);
                    Toast.makeText(getContext(), "Could not update profile, please try again.", Toast.LENGTH_SHORT).show();
                    // Make save button pressable again so user can try again
                    btnSave.setEnabled(true);
                }));
    }
}
