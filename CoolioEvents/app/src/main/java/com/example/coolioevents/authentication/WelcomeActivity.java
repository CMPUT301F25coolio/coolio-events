package com.example.coolioevents.authentication;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;

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
 * 2 buttons options - login and signup
 *
 * RATIONALE:
 * This class was designed to welcome users into the app and have them
 * choose to login or sign up.
 *
 * @author Ethan Diep
 * @version 1.0
 * @since 2025-11-06
 */
public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton, signupButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // Force logout so app always asks to sign in (good for testing)
        // Delete this line later if you want auto-login behavior.
        mAuth.signOut();

        loginButton  = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));
        signupButton.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, SignupActivity.class)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Ask user for permission to recieve notifications if they have not allowed it yet
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    /**
     * Sets the FirebaseAuth, which logs users into the app.
     * @param mAuth The system that logs users into the app.
     */
    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }



    // No onStart auto-redirect. User must tap Login/Sign Up.
}
