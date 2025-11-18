package com.example.coolioevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coolioevents.R;

public class ListScreenActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "eventId";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        ImageButton back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());

        findViewById(R.id.cardWait).setOnClickListener(v -> openList(eventId, EventEntrantListActivity.TYPE_WAIT));
        findViewById(R.id.cardChosen).setOnClickListener(v -> openList(eventId, EventEntrantListActivity.TYPE_CHOSEN));
        findViewById(R.id.cardCancelled).setOnClickListener(v -> openList(eventId, EventEntrantListActivity.TYPE_CANCELLED));
    }

    private void openList(String eventId, int type){
        Intent i = new Intent(this, EventEntrantListActivity.class);
        i.putExtra(EXTRA_EVENT_ID, eventId);
        i.putExtra(EventEntrantListActivity.EXTRA_TYPE, type);
        startActivity(i);
    }
}
