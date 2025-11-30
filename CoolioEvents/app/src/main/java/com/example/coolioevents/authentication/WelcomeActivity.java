package com.example.coolioevents.authentication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.Entrant.EntrantActivity;
import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
 * This class represents the Welcome Activity
 * It displays a screen which welcomes the user into the app, along with
 * 3 options: login, sign up, or continue as an entrant identified by device.
 *
 * RATIONALE:
 * This class was designed to welcome users into the app and have them
 * choose to login, sign up, or use device-based entrant identification.
 *
 * @author Ethan Diep
 * @version 1.1
 * @since 2025-11-06
 */
/*
 * Welcome screen – lets users log in, sign up,
 * and for entrants supports device based identification.
 */
public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton, signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // No one signed in on this device then normal welcome screen
            showWelcomeUI();
            return;
        }

        // Someone is signed in (could be organizer or entrant) then check Firestore role
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");
                        if ("Entrant".equalsIgnoreCase(role)) {
                            // Device belongs to an entrant: auto-login, skips welcome or login
                            Intent i = new Intent(WelcomeActivity.this, EntrantActivity.class);
                            startActivity(i);
                            finish();
                            return;
                        }
                    }
                    // Not an entrant or missing doc directs to normal welcome screen
                    showWelcomeUI();
                })
                .addOnFailureListener(e -> {
                    // On any error fall back to normal welcome
                    showWelcomeUI();
                });
    }


    /**
     * Shows the existing welcome UI (login + signup buttons).
     * This is the old onCreate body extracted into a method.
     */
    private void showWelcomeUI() {
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return insets;
        });

        loginButton  = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Existing flows – unchanged
        loginButton.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));
        signupButton.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, SignupActivity.class)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }


     // Starts an entrant session that is identified by this device only.

    private void startEntrantByDevice() {
        FirebaseUser current = mAuth.getCurrentUser();

        // If we already have an anonymous user on this device, reuse it.
        if (current != null && current.isAnonymous()) {
            startActivity(new Intent(WelcomeActivity.this, EntrantActivity.class));
            finish();
            return;
        }

        // If a non-anonymous user is logged in (organizer/admin), sign them out
        // and start an anonymous entrant session instead.
        if (current != null && !current.isAnonymous()) {
            mAuth.signOut();
        }

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(WelcomeActivity.this,
                                "Could not start entrant session.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser anonUser = mAuth.getCurrentUser();
                    if (anonUser == null) {
                        Toast.makeText(WelcomeActivity.this,
                                "Could not load entrant user.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String uid = anonUser.getUid();

                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(doc -> {
                                if (!doc.exists()) {
                                    // First time this device is used as an entrant – create user doc.
                                    Map<String, Object> usermap = new HashMap<>();
                                    usermap.put("role", "Entrant");
                                    usermap.put("name", "Device Entrant");
                                    usermap.put("username", "entrant_" + uid.substring(0, 8));
                                    usermap.put("email", null);

                                    db.collection("users").document(uid).set(usermap);
                                }

                                startActivity(new Intent(WelcomeActivity.this, EntrantActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(WelcomeActivity.this,
                                            "Error creating entrant: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                });
    }

     // For tests/mocking

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }


}
