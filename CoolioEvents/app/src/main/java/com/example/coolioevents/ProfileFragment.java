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

import androidx.appcompat.app.AlertDialog;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.coolioevents.Entrant.EntrantSettingsFragment;
import com.example.coolioevents.authentication.WelcomeActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A Fragment that displays the current user's profile.
 * It retrieves the data from Firestore using the authenticated user's ID.
 * If the user is not logged in, an appropriate message is displayed.
 */
public class ProfileFragment extends Fragment {
    // Switch for notification
    private SwitchMaterial notificationSwitch;
    private boolean userChecked = true;

    // TextView displaying the user's profile
    private TextView profileText;

    // ImageView for profile circle
    private ImageView profileCircle;

    // TextView displaying the user's username
    private TextView textUsername;

    // TextView displaying the user's full name
    private TextView textName;

    // TextView displaying the user's email address
    private TextView textEmail;

    // Button allowing the user to logout
    private Button btnEditProfile;

    // Button allowing the user to navigate to the UpdateProfileFragment
    private Button logoutButton;
    private Button deleteAccountButton;

    // Firebase Authentication instance used to identify the logged-in user
    private FirebaseAuth auth;

    // Firebase Firestore instance used to fetch user profile data
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty constructor
    }

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
        profileCircle = view.findViewById(R.id.profile_circle);
        profileText = view.findViewById(R.id.icon_text);
        textUsername = view.findViewById(R.id.text_username);
        textName = view.findViewById(R.id.text_name);
        textEmail = view.findViewById(R.id.text_email);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteAccountButton = view.findViewById(R.id.btn_delete_account);

        notificationSwitch = view.findViewById(R.id.notificationSwitch);

        checkPermissions();

        // Notification switch to turn on/off notifications
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (userChecked){
                    // Checks to see if a real user, is clicking the notification
                    // or if its the program (if its the program don't prompt user with anything)
                    if (isChecked){
                        showSettingsPrompt("on");
                    }
                    else {
                        // If user turned switch off
                        showSettingsPrompt("off"); // Tell user they must go to app settings to turn off notifications
                    }
                }
                else {
                    userChecked = true;
                }
            }
        });

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

        logoutButton.setOnClickListener(v -> {
            logout(); // If logout button pressed - perform logout
        }
        );

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

                        textUsername.setText((username != null ? username : ""));
                        textName.setText((name != null ? name : ""));
                        textEmail.setText((email != null ? email : ""));
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
     * This method signs the user out of their account - it
     * signs out of the current user in mAuth, and
     * sends user back to the welcome screen
     */
    private void logout(){
        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        auth.signOut();
        startActivity(intent);
        requireActivity().finish();
    }
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        // 1. Delete Firestore user document
        db.collection("users").document(uid).delete()
                .addOnSuccessListener(unused -> {
                    // 2. Delete FirebaseAuth user
                    auth.getCurrentUser().delete()
                            .addOnSuccessListener(unused2 -> {
                                Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                                // 3. Log out and go to WelcomeActivity
                                Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * This method creates and shows a new alert dialog which
     * informs the user that notificaitons can only be turned off via settings
     * and allows them to go to settings if they press "Go to settings" on the alert dialog
     *
     * @param state
     *  Tells the what the settings dialog should say (whether to tel user to turn off or on notifications)
     */
    private void showSettingsPrompt(String state){
        new AlertDialog.Builder(requireActivity())
                .setTitle(String.format("Turn Notifications %s In Settings", state))
                .setMessage(String.format("To turn %s notifications please turn %s post notifications in settings.", state, state))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermissions(); // If cancelled, check permissions again and change switch as necessary
                        dialog.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        checkPermissions(); // If cancelled, check permissions again and change switch as necessary
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*Taken from: Google Gemini
                        Prompt: how to go to settings app from android app android studio java in an fragment action application settings
                        Taken by: Ethan Diep
                        Taken on: 11/25/25*/
                        // Sends user to settings to change notification settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        // When user is back form settings or resumes fragment, checks
        // Permissions again to see if notification permissions are changed
        checkPermissions();
    }

    /**
     * This method checks perimssions of user - whether their notifications are turned
     * on or off - and switches notificationSwitch depending on what their current
     * notification settings are.
     */
    private void checkPermissions(){
        userChecked = false; // The next switch on the switch is not a "real user" switching the fragment
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Notifications permissions are on - set switch to on
            notificationSwitch.setChecked(true);
        }
        else {
            // Notifications permissions are off - set switch to off
            notificationSwitch.setChecked(false);
        }
        userChecked = true; // Set userChecked back to true to allow user to switch the switch again
    }
}
