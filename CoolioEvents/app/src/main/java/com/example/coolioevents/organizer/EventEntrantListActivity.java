package com.example.coolioevents.organizer;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coolioevents.R;
import com.example.coolioevents.repo.EntrantsRepository;

import java.util.List;

/**
 * Displays Waitlist / Chosen / Cancelled entrants
 * with two columns:
 *   Entrants | Registered (Yes/No)
 */
public class EventEntrantListActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "listType";
    public static final int TYPE_WAIT = 0;
    public static final int TYPE_CHOSEN = 1;
    public static final int TYPE_CANCELLED = 2;

    private EntrantStatusAdapter adapter;
    private EntrantsRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_entrant_list);

        String eventId = getIntent().getStringExtra(ListScreenActivity.EXTRA_EVENT_ID);
        int type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_WAIT);

        // Back button
        ImageButton back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());

        // Title
        TextView title = findViewById(R.id.titleText);
        title.setText(titleFor(type));

        // Recycler setup
        RecyclerView rv = findViewById(R.id.recyclerEntrants);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantStatusAdapter();
        rv.setAdapter(adapter);

        repo = new EntrantsRepository();
        load(eventId, type);
    }

    private void load(String eventId, int type) {
        if (eventId == null || eventId.isEmpty()) {
            toast("Missing eventId");
            return;
        }

        switch (type) {
            case TYPE_WAIT:
                repo.getWaitlist(eventId)
                        .addOnSuccessListener(ids -> bind(ids, false))
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;

            case TYPE_CHOSEN:
                repo.getChosen(eventId)
                        .addOnSuccessListener(ids -> bind(ids, true))
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;

            case TYPE_CANCELLED:
                repo.getFinalEnrolled(eventId)
                        .addOnSuccessListener(ids -> bind(ids, false))
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;
        }
    }

    /** Bind to adapter with proper Yes/No value */
    private void bind(List<String> ids, boolean registeredYes) {
        adapter.update(ids, registeredYes);
    }

    private String titleFor(int type) {
        switch (type) {
            case TYPE_WAIT: return "Wait List";
            case TYPE_CHOSEN: return "Entrant List";
            case TYPE_CANCELLED: return "Cancelled Entrant List";
        }
        return "Entrants List";
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
