package com.example.coolioevents.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {


    EditText nameEditText; // edittext where user inputs full name
    EditText usernameEditText; // edittext where user inputs username
    EditText emailEditText; // edittext where user inputs email
    EditText passwordEditText; // edittext where user inputs password


    Button organizerButton; //choose organizer button
    Button entrantButton; //choose entrant button
    TextView warnText; // textview used for warnings (eg. invalid password, email, etc.)

    Button createAccountButton; // button which creates account
    Boolean usernameExists; // textview used for warnings (eg. invalid password, email, etc.)
    ArrayList<String> usernamelist; // Contains all usernames in firestore database
    int accountType; // -1 is not selected, 0 is organizer, 1 is entrant

    public void setUsernameExists(Boolean usernameExists) {
        this.usernameExists = usernameExists;
    }

    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database
    private CollectionReference userCollection; // collection of users in firebase database
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

        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        entrantButton = findViewById(R.id.entrantButton);
        organizerButton = findViewById(R.id.organizerButton);
        warnText = findViewById(R.id.warnText);

        createAccountButton = findViewById(R.id.createAccountButton);

        accountType = -1; // Initally Account type selected is none,

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        usernamelist = new ArrayList<String>();

        //Snapshot listener which updates username list to keep track of all usernames
        userCollection.addSnapshotListener((value, error) -> {
            if (value !=null && !value.isEmpty()){
                usernamelist.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    String username = snapshot.getString("username");
                    usernamelist.add(username);
                }
            }
        });

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

        // On Click Listener for Create Account - takes in user's email and password as a string
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    //If somebody is already using the username provided, warn the user
                    warnText.setText("Somebody with the same username already exists");

                    return;
                }
                if (TextUtils.isEmpty(email)){
                    // If Email provided is empty warn user they need to put in an email
                    warnText.setText("Please put in an email");
                    return;

                }
                if (TextUtils.isEmpty(password)){
                    // If Password pr             warnText.setText("Please put in a password");ovided is empty warn user they need to put in an email

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
        });
    }



}