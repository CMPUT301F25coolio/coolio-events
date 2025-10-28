package com.example.coolioevents.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Welcome screen. Always shows Login/Sign Up and forces logout on launch (testing mode).
 * Remove the signOut() line if you want persistent sessions later.
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
    }

    // No onStart auto-redirect. User must tap Login/Sign Up.
}
