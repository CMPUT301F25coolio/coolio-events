package com.example.coolioevents.Entrant;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.R;

public class EntrantCriteriaGuidelinesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_criteria_guidelines);

        FrameLayout backButton = findViewById(R.id.btnBack);

        // Back button onclick activity --> Leads to previous activity (Profile or Edit Profile Fragment)
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    finish());
        }
    }
}