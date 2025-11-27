package com.example.coolioevents.organizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizerUpdateProfileActivity extends AppCompatActivity {
    private TextView profileText;
    private ImageView profileCircle;
    private EditText editUsername;
    private EditText editName;
    private EditText editEmail;
    private Button btnSave;
    private Button btnBack;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_update_profile);


        // Initialize input fields and button
        profileCircle = findViewById(R.id.update_profile_circle);
        profileText = findViewById(R.id.icon_text);
        editUsername = findViewById(R.id.edit_username);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        btnSave = findViewById(R.id.btn_save_profile);
        btnBack = findViewById(R.id.btn_back_profile);

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

            // Navigate back to the previous activity (OrganizerProfileActivity)
            finish();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadEditText() {
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

                        editUsername.setText((username != null ? username : ""));
                        editName.setText((name != null ? name : ""));
                        editEmail.setText((email != null ? email : ""));
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
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
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

        // Disable the save button to prevent multiple clicks
        btnSave.setEnabled(false);

        // Actually do the update
        userRef.update(updates)
            .addOnSuccessListener(aVoid -> {
                /*
                Taken From: Google Gemini
                    Prompt: why am I getting a NullPointerException?
                    Taken By: Avery Dancocks
                    Taken On: 11/26/25
                 */
                if (isFinishing() || isDestroyed()) {
                    return; // Stop execution if the Activity is no longer valid.
                }
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                // Go back to profile
                //TODO getParentFragmentManager().popBackStack();
            })
            .addOnFailureListener((e -> {
                if (isFinishing() || isDestroyed()) {
                    return; // Stop execution if the Activity is no longer valid.
                }

                Log.e("UpdateProfileFragment", "Error updating profile", e);
                Toast.makeText(this, "Could not update profile, please try again.", Toast.LENGTH_SHORT).show();
                // Make save button pressable again so user can try again
                btnSave.setEnabled(true);
            }));
    }
}
