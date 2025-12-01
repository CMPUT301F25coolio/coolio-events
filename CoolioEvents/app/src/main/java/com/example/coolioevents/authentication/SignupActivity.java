package com.example.coolioevents.authentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.Entrant.Entrant;
import com.example.coolioevents.Entrant.EntrantActivity;
import com.example.coolioevents.R;
import com.example.coolioevents.organizer.Organizer;
import com.example.coolioevents.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2025 Ethan Diep
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
 * This class represents the Signup Activity
 * It displays field a user needs to sign up for an account
 * including name, username, email, password, and role (organizer or entrant)
 * When done, the user can press create account and the account is made on Firestore
 * Authenticator and Firestore Firebase.
 *
 * RATIONALE:
 * This class was designed to provide users with a way to create an account
 *
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-06
 */
public class SignupActivity extends AppCompatActivity {


    EditText nameEditText; // Edittext where user inputs full name
    EditText usernameEditText; // Edittext where user inputs username
    EditText emailEditText; // Edittext where user inputs email
    EditText passwordEditText; // Edittext where user inputs password

    ImageButton backButton; // Button to go back to welcome screen
    Button organizerButton; // Choose organizer button
    Button entrantButton; // Choose entrant button
    TextView warnText; // Textview used for warnings (eg. invalid password, email, etc.)
    Button createAccountButton; // Button which creates account
    Boolean usernameExists; // Textview used for warnings (eg. invalid password, email, etc.)
    ArrayList<String> usernamelist; // Contains all usernames in firestore database
    int accountType; // -1 is not selected, 0 is organizer, 1 is entrant

    public void setUsernameExists(Boolean usernameExists) {
        this.usernameExists = usernameExists;
    }

    private FirebaseAuth mAuth; // Authenticator to create user accounts
    private FirebaseFirestore db; // Database
    private CollectionReference userCollection; // Collection of users in firebase database
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, 0, systemBars.bottom);
            return insets;
        });
        ImageView topWave = findViewById(R.id.topWave);
        ScrollView formScroll = findViewById(R.id.formScroll);
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        backButton = findViewById(R.id.backButton);
        entrantButton = findViewById(R.id.entrantButton);
        organizerButton = findViewById(R.id.organizerButton);
        warnText = findViewById(R.id.warnText);

        createAccountButton = findViewById(R.id.createAccountButton);

        accountType = -1; // Initally Account type selected is none,

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        usernamelist = new ArrayList<String>();

        // Makes Snapshot listener which updates username list to keep track of all usernames
        addUsernameSL();

        // Back Button On Click Listener - sends user back to welcome screen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });

        // Organizer Button On Click Listener - User chooses their account type as organizer
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAccountType(0);
            }
        });

        // Organizer Button On Click Listener - User chooses their account type as organizer
        entrantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAccountType(1);
            }
        });

        // On Click Listener for Create Account - takes in user's email and password as a string (https://www.youtube.com/watch?v=QAKq8UBv4GI oct25)
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignup();
            }
        });
        //please don't remove this, it's to make the scroll ui better
        topWave.post(() -> {
            int headerHeight = topWave.getHeight();
            // Push content down so it starts below the wave
            formScroll.setPadding(
                    formScroll.getPaddingLeft(),
                    headerHeight,
                    formScroll.getPaddingRight(),
                    formScroll.getPaddingBottom()
            );
            formScroll.setClipToPadding(false); // important: lets content scroll into padded area
        });
    }

    /**
     * Sets up Snapshotlistener for users to keep update list of all usernames in usernamelist
     */
    private void addUsernameSL(){
        userCollection.addSnapshotListener((value, error) -> {
            if (value !=null && !value.isEmpty()){
                usernamelist.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    String username = snapshot.getString("username");
                    usernamelist.add(username);
                }
            }
        });
    }

    /**
     * Changes account type to Entrant if 0, Organizer if 1, and and update button colors
     * @param type the type of account; 0 for Entrant, 1 for Organizer
     */
    private void changeAccountType(int type){

        if (type == 0) {
            // Change account type to Entrant and update button colors
            accountType = 0;
            organizerButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.purplecircularbackground));
            entrantButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.inactive_button_border));
        } else if (type == 1) {
            // Change account type to Organizer and update button colors
            accountType = 1;
            entrantButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.purplecircularbackground));
            organizerButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.inactive_button_border));

        }
    }

    /**
     * Goes through signup process for user
     */
    private void doSignup(){
        String name, username, email, password;
        name = nameEditText.getText().toString();
        username = usernameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        usernameExists = false;
        warnText.setText("");

        if (TextUtils.isEmpty(name)){
            // If name  provided is empty warn user they need to put in an name
            warnText.setText("Please put in your Full name");
            return;

        }
        if (TextUtils.isEmpty(username)){
            // If username provided is empty warn user they need to put in an username
            warnText.setText("Please put in a username");
            return;

        }
        if (usernamelist.contains(username)) {
            // If somebody is already using the username provided, warn the user
            warnText.setText("Somebody with the same username already exists");

            return;
        }
        if (TextUtils.isEmpty(email)){
            // If Email provided is empty warn user they need to put in an email
            warnText.setText("Please put in an email");
            return;

        }
        if (TextUtils.isEmpty(password)){
            // If Password provided is empty warn user they need to put in a password
            warnText.setText("Please put in a password");

            return;
        }
        if (accountType == -1){
            //If user has not selected a account type yet, warn them
            warnText.setText("Please select an account type");
            return;
        }
        // Attempts to create an account on firebase
        attemptMakeAccount(name, username, email, password);
    }

    /**
     * Attempts to make an account for the user. Adds their account to the database if successful.
     * @param name The user's name that they provided
     * @param username The user's username that they provided
     * @param email The user's email that they provided
     * @param password The user's password that they provided
     */
    private void attemptMakeAccount(String name, String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If sign in is successful, show a toast
                            Toast.makeText(SignupActivity.this, "Account Created.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser(); // User on mauth

                            Map<String, Object> usermap = new HashMap<>();
                            usermap.put("name", name);
                            usermap.put("username",username);
                            usermap.put("email", user.getEmail());
                            if (accountType == 0) {
                                // If user selected Organizer as account type, set their role as Organizer
                                // Send user to OrganizerActivity
                                usermap.put("role", "Organizer");
                                db.collection("users").document(user.getUid()).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(SignupActivity.this, OrganizerActivity.class));
                                    }
                                });
                            } else if (accountType == 1) {
                                // If user selected Entrant as account type, set their role as Organizer
                                // Send user to EntrantActivity
                                usermap.put("role", "Entrant");
                                db.collection("users").document(user.getUid()).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(SignupActivity.this, EntrantActivity.class));
                                    }
                                });
                            }
                        }
                        else {
                            // If sign in is unsuccessful, show a toast
                            Toast.makeText(SignupActivity.this, "Account Creation failed.",
                                    Toast.LENGTH_SHORT).show();
                            if (task.getException() != null){
                                // Tell user what they need to fix
                                warnText.setText(task.getException().getMessage());
                            }
                        }
                    }
                });
    }





}