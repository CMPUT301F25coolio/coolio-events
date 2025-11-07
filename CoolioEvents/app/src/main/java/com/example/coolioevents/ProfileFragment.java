package com.example.coolioevents;

/**
 * Copyright 2025 Niharika Rawat
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
 * This fragment displays the logged-in user's profile information such as
 * username, name, and email. It fetches the user data from Firebase Firestore
 * and allows navigation to an update screen for editing profile details.
 *
 * The fragment works for both Entrants and Organizers within the CoolioEvents app.
 *
 * @author Niharika Rawat
 * @version 1.0
 * @since 2025-11-07
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A Fragment that displays the current user's profile.
 * It retrieves the data from Firestore using the authenticated user's ID.
 * If the user is not logged in, an appropriate message is displayed.
 */
public class ProfileFragment extends Fragment {

    /** TextView displaying the user's username */
    private TextView textUsername;

    /** TextView displaying the user's full name */
    private TextView textName;

    /** TextView displaying the user's email address */
    private TextView textEmail;

    /** Button allowing the user to navigate to the UpdateProfileFragment */
    private Button btnEditProfile;

    /** Firebase Authentication instance used to identify the logged-in user */
    private FirebaseAuth auth;

    /** Firebase Firestore instance used to fetch user profile data */
    private FirebaseFirestore db;

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     * This method inflates the fragment layout, initializes Firebase services,
     * loads the user's profile, and sets up the Edit Profile button.
     *
     * @param inflater LayoutInflater to inflate the fragment layout
     * @param container Optional parent view that the fragment UI attaches to
     * @param savedInstanceState Saved state data, if any
     * @return The created View for this fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate layout for the fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        textUsername = view.findViewById(R.id.text_username);
        textName = view.findViewById(R.id.text_name);
        textEmail = view.findViewById(R.id.text_email);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load user's profile from Firestore
        loadUserProfile();

        // Navigate to the update profile screen when the button is clicked
        btnEditProfile.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UpdateProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
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
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the user ID
        String userId = auth.getCurrentUser().getUid();

        // Reference the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(userId);

        // Fetch data asynchronously
        userRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String username = document.getString("username");
                        String name = document.getString("name");
                        String email = document.getString("email");

                        textUsername.setText("Username: " + (username != null ? username : ""));
                        textName.setText("Name: " + (name != null ? name : ""));
                        textEmail.setText("Email: " + (email != null ? email : ""));
                    } else {
                        Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error fetching profile", e);
                    Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                });
    }
}
