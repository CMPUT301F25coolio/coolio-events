package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Entrant.UserViewModel;
import com.example.coolioevents.R;
import com.example.coolioevents.UpdateProfileFragment;
import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Button deleteButton;
    private FrameLayout btnBack;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_profile);

        // Initialize viewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize UI elements
        profileCircle = findViewById(R.id.profile_circle);
        profileText = findViewById(R.id.icon_text);
        textUsername = findViewById(R.id.text_username);
        textName = findViewById(R.id.text_name);
        textEmail = findViewById(R.id.text_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnBack = findViewById(R.id.btnBack);
        logoutButton = findViewById(R.id.logoutButton);
        deleteButton = findViewById(R.id.deleteAccountButton);

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load user's profile from Firestore
        loadUserProfile();

        // Navigate to the update profile screen when the button is clicked
        btnEditProfile.setOnClickListener(v -> {
            promptPasswordEditProfile();
        });

        logoutButton.setOnClickListener(v -> {
            logout(); // If logout button pressed - perform logout
        });

        // Delete button -> delete organizer from database
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

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

                        if (name != null) {
                            String initials = getInitials(name);
                            profileText.setText(initials);
                        }

                        textUsername.setText((username != null ? username : ""));
                        textName.setText((name != null ? name : ""));
                        textEmail.setText(auth.getCurrentUser().getEmail());
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

    /**
     * This function shows the dialogue to confirm deleting the account
     * or to cancel
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> showReauthDialog())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * This method shows a dialog asking the user to re-enter their password
     * before deleting their account : like a confirmation
     * If password incorrect : account deleting won't go through
     */
    private void showReauthDialog() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialogue_reauth, null);
        EditText etPassword = view.findViewById(R.id.etPassword);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Password")
                .setView(view)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String password = etPassword.getText().toString().trim();
                    if (password.isEmpty()) {
                        Toast.makeText(this,
                                "Password cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reauthenticateAndDelete(user, password);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * This method reauthenticates the user with their password
     */
    private void reauthenticateAndDelete(FirebaseUser user, String password) {
        String email = user.getEmail();
        if (email == null) {
            Toast.makeText(this,
                    "No email associated with this account.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {
                    Log.d("ProfileFragment", "Re-authentication successful");
                    performAccountDeletion(user);
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Re-authentication failed", e);
                    Toast.makeText(this,
                            "Re-authentication failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Remove this user from from users and remove all of their events
     *
     * After successful re-authentication, actually delete everything:
     * 1) delete all of the events they made
     * 2) delete Firestore user doc
     * 3) delete FirebaseAuth user
     */
    private void performAccountDeletion(FirebaseUser user) {
        String uid = user.getUid();

        if (userViewModel != null) {
            // Delete from database
            userViewModel.deleteUser(uid);

            // Delete all events
            userViewModel.deleteEventsMadeByOrganizer(uid);

            // Deleting from Firebase Auth
            // 3. Delete FirebaseAuth user (now recently re-authenticated)
            user.delete()
                    .addOnSuccessListener(unusedAuth -> {
                        Toast.makeText(this,
                                "Account deleted",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, WelcomeActivity.class);
                        startActivity(intent);
                        this.finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileFragment", "Failed to delete auth user", e);
                        Toast.makeText(this,
                                "Failed to delete auth user: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });


        }
    }
    /**
     * Prompts user with Alert Dialog to enter their password in order to update their profile.
     */
    private void  promptPasswordEditProfile(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialogue_reauth, null);
        EditText etPassword = view.findViewById(R.id.etPassword);
        TextView DialogMessage = view.findViewById(R.id.messageLabel);

        DialogMessage.setText("Confirm password to Edit Profile");
        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String password = etPassword.getText().toString().trim();
                    if (password.isEmpty()) {
                        Toast.makeText(this,
                                "Password cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    authenticateEditProfile(password);
                })
                .setNegativeButton("Cancel", null)
                .show();

    }
    /**
     * Method that checks to see if password provided is correct, if correct
     * send user to update profile activity
     */
    private void authenticateEditProfile(String password){
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        if (email == null) {
            Toast.makeText(this,
                    "No email associated with this account.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {
                    Log.d("ProfileFragment", "Re-authentication successful");
                    goToUpdateProfile();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Re-authentication failed", e);
                    Toast.makeText(this,
                            "Re-authentication failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
    /**
     * Send user to update profile activity
     */
    private void goToUpdateProfile(){
        startActivity(new Intent(this, OrganizerUpdateProfileActivity.class));
    }



}
