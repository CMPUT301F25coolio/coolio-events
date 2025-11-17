package com.example.coolioevents.administrator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coolioevents.R;

public class AdministratorEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrator_events);

        // Establishing views
        ImageButton backButton = findViewById(R.id.btnBack);

        // Back button onclick activity --> Leads to Home activity
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    startActivity(new Intent(this, AdministratorHomeActivity.class)));
        }


    }
}