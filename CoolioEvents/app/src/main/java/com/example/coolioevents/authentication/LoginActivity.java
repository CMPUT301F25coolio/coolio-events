package com.example.coolioevents.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.Entrant.EntrantActivity;
import com.example.coolioevents.R;
import com.example.coolioevents.organizer.OrganizerActivity; // <-- use this (it exists)
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private TextView warnText;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    Button backButton; //button to go back to welcome screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        warnText = findViewById(R.id.warnText);
        backButton = findViewById(R.id.backButton);
        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(v -> doLogin());

        // Back Button On Click Listener - sends user back to welcome screen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void doLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        warnText.setText("");
        if (TextUtils.isEmpty(email)) { warnText.setText("Please put in an email"); return; }
        if (TextUtils.isEmpty(password)) { warnText.setText("Please put in a password"); return; }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(LoginActivity.this, "Login Failed (no user).", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(LoginActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        // Fetch THIS user's role directly and route.
                        db.collection("users").document(user.getUid()).get()
                                .addOnSuccessListener(doc -> {
                                    String role = doc.getString("role");
                                    if ("Organizer".equals(role)) {
                                        startActivity(new Intent(LoginActivity.this, OrganizerActivity.class));
                                        finish();
                                    } else if ("Entrant".equals(role)) {
                                        startActivity(new Intent(LoginActivity.this, EntrantActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Role not found. Contact admin.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(LoginActivity.this, "Could not load role: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
