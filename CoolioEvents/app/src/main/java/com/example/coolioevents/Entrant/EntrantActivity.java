package com.example.coolioevents.Entrant;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.coolioevents.Event;
import com.example.coolioevents.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EntrantActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; //  authenticator to create user accounts
    private FirebaseFirestore db; // database
    private CollectionReference userCollection; // collection of users in firebase database

    private FirebaseUser user; //The current user


    private Fragment homeFragment; //Fragment for home screen
    private Fragment myEventsFragment; //Fragment for home screen
    private BottomNavigationView bottomNavView;



    private Map<String, Entrant> entrantMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant);
        homeFragment = new EntrantHomeFragment();
        myEventsFragment = new EntrantMyEventsFragment();

        SwitchFragment(homeFragment); //Default fragment is home

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        user = mAuth.getCurrentUser();
        bottomNavView = findViewById(R.id.bottomNavigationView);


        SwitchFragment(homeFragment);

        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home){
                    // If home item is selected in nav bar switch to home fragment
                    SwitchFragment(homeFragment);
                } else if (menuItem.getItemId() == R.id.myevents) {
                    // If myevents item is selected in nav bar switch to home fragment
                    SwitchFragment(myEventsFragment);
                }

                return false;
            }
        });
    }

    public void SwitchFragment(Fragment fragment){
        // Switches fragment to fragment in the fragment container
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.fragment_container, fragment);
        fragTransaction.commit();
    }
}