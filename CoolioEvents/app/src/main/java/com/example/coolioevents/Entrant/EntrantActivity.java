package com.example.coolioevents.Entrant;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.Profile;
import com.example.coolioevents.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class EntrantActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database
    private CollectionReference userCollection; // collection of users in firebase database

    private FirebaseUser user; //The current user

    private TextView info;

    private Map<String, Entrant> entrantMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        user = mAuth.getCurrentUser();
        info = findViewById(R.id.infoText);
        entrantMap = new HashMap<>();

        userCollection.addSnapshotListener((value, error) -> {
            if (value !=null && !value.isEmpty()){
                entrantMap.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    String userid = snapshot.getId();
                    System.out.println(snapshot.getId());
                    String role = snapshot.getString("role");
                    System.out.println(role);
                    String email = snapshot.getString("email");
                    System.out.println(email);
                    String name = snapshot.getString("name");
                    String username = snapshot.getString("username");
                    if (role != null && role.equals("Entrant")){
                        entrantMap.put(userid, new Entrant(new Profile(userid, username, name, email)));
                        if (userid.equals(user.getUid())) {
                            Profile currentEntrantProfile = entrantMap.get(user.getUid()).getProfile();
                            info.setText(String.format("username: %s\nname: %s\nemail: %s", currentEntrantProfile.getUsername(), currentEntrantProfile.getName(), currentEntrantProfile.getEmail()));
                        }
                    }
                }
            }
        });


    }
}