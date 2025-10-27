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
import com.example.coolioevents.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText; // edittext where user inputs email
    EditText passwordEditText; // edittext where user inputs email

    TextView warnText; // edittext where user inputs email
    Button loginButton; // edittext where user inputs email
    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database
    private CollectionReference userCollection;
    Map<String, String> userRoles; // Map containing each user's role (Entrant or Organizer)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        warnText = findViewById(R.id.warnText);

        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        userRoles = new HashMap<>();
        userCollection.addSnapshotListener((value, error) -> {
            if (value !=null && !value.isEmpty()){
                userRoles.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    String username = snapshot.getString("username");
                    userRoles.put(snapshot.getId(), snapshot.getString("role"));
                }
            }
        });
        // Login button on click listner - allows user to login to their account if valid details provided
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                warnText.setText("");
                if (TextUtils.isEmpty(email)){
                    // If Email provided is empty warn user they need to put in an email
                    warnText.setText("Please put in an email");
                    return;

                }
                if (TextUtils.isEmpty(password)){
                    // If password provided is empty warn user they need to put in an email
                    warnText.setText("Please put in a password");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, String.format("Welcome: %s", user.getEmail()),
                                            Toast.LENGTH_SHORT).show();

                                    if (userRoles.get(user.getUid()).equals("Organizer")){
                                        //If the user that signed in has role organizer, send them to organizer activity
                                        startActivity(new Intent(LoginActivity.this, OrganizerActivity.class));

                                    } else if (userRoles.get(user.getUid()).equals("Entrant")) {
                                        //If the user that signed in has role organizer, send them to entrant activity
                                        startActivity(new Intent(LoginActivity.this, EntrantActivity.class));
                                    }


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Login Failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }
}