package com.example.coolioevents;

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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {
    EditText emailEditText; // edittext where user inputs email
    EditText passwordEditText; // edittext where user inputs password


    Button organizerButton; //choose organizer button
    Button entrantButton; //choose entrant button
    TextView warnText; // textview used for warnings (eg. invalid password, email, etc.)

    Button createAccountButton; // button which creates account

    int accountType; // -1 is not selected, 0 is organizer, 1 is entrant
    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        entrantButton = findViewById(R.id.entrantButton);
        organizerButton = findViewById(R.id.organizerButton);
        warnText = findViewById(R.id.warnText);

        createAccountButton = findViewById(R.id.createAccountButton);

        accountType = -1; // Initally Account type selected is none,

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Organizer Button On Click Listener - User chooses their account type as organizer
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountType = 0;
                organizerButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.bluebuttonbackground));
                entrantButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.greybuttonbackground));

            }
        });

        // Organizer Button On Click Listener - User chooses their account type as organizer
        entrantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountType = 1;
                entrantButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.bluebuttonbackground));
                organizerButton.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.greybuttonbackground));
            }
        });






        // On CLick Listener for Create Account - takes in user's email and password as a string
        createAccountButton.setOnClickListener(new View.OnClickListener() {
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
                    // If Password provided is empty warn user they need to put in an email
                    warnText.setText("Please put in a password");
                    return;
                }
                if (accountType == -1){
                    //If user has not selected a account type yet, warn them
                    warnText.setText("Please select an account type");
                    return;
                }
                // Attempts to create an account on firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // If sign in is successful, show a toast
                                    Toast.makeText(SignupActivity.this, "Account Created.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser(); // user on mauth

                                    Map<String, Object> usermap = new HashMap<>();
                                    usermap.put("email", user.getEmail());
                                    if (accountType == 0) {
                                        // If user selected Organizer as account type, set their role as Organizer
                                        usermap.put("role", "Organizer");
                                    } else if (accountType == 1) {
                                        // If user selected Entrant as account type, set their role as Organizer
                                        usermap.put("role", "Entrant");
                                    }
                                    db.collection("users").document(user.getUid()).set(usermap);
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
        });
    }
}