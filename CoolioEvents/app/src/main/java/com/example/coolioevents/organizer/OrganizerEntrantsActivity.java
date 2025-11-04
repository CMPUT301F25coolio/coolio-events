package com.example.coolioevents.organizer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coolioevents.R;
import com.example.coolioevents.repo.EntrantsRepository;
import java.util.List;
/*
  Screen where I can type an event id and peek at different entrant lists
  waiting,chosen,final. This basically reads a couple arrays from the event doc.
  Why a separate Activity cuz Keeps it isolated so I don't mess with team flows*/
public class OrganizerEntrantsActivity extends AppCompatActivity {
    // data helper that actually talks to Firestore
    private EntrantsRepository repo;
    // recycler adapter that just shows a bunch of strings
    private EntrantIDAdapter adapter;
    // lazy shortcut array so I don't hardcode strings in a switch every time
    private static final String[] LIST_OPTIONS = new String[]{
            "Waiting List",      // index 0
            "Chosen / Invited",  // index 1
            "Final Enrolled"     // index 2
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_entrants);
        // init things I need
        repo = new EntrantsRepository();
        EditText eventIdEt = findViewById(R.id.eventIdInput);
        Spinner listType = findViewById(R.id.spinnerListType);
        Button loadBtn = findViewById(R.id.btnLoad);
        RecyclerView recycler = findViewById(R.id.recycler);
        // recycler setup (basic vertical list)
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantIDAdapter();
        recycler.setAdapter(adapter);
        // spinner items I used a small array constant above
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, LIST_OPTIONS
        );
        listType.setAdapter(spinnerAdapter);
        // if someone launched me with an eventId already, just fill it in
        String prefill = getIntent().getStringExtra("eventId");
        if (prefill != null && !prefill.isEmpty()) {
            eventIdEt.setText(prefill);
        }
        // main button that reads whichever list the user picked
        loadBtn.setOnClickListener(v -> {
            String eventId = eventIdEt.getText().toString().trim();
            if (eventId.isEmpty()) {
                toast("Enter eventId");
                return;
            }
            int choice = listType.getSelectedItemPosition();
            loadListForChoice(eventId, choice);
        });
    }
    /*
      Decides which Firestore array to grab based on spinner index.
      I split it out so onClick stays short.*/
    private void loadListForChoice(String eventId, int index) {
        switch (index) {
            case 0: // Waiting List
                repo.getWaitlist(eventId)
                        .addOnSuccessListener(this::show)
                        .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
                break;
            case 1: // Chosen / Invited
                repo.getChosen(eventId)
                        .addOnSuccessListener(this::show)
                        .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
                break;
            case 2: // Final Enrolled
                repo.getFinalEnrolled(eventId)
                        .addOnSuccessListener(this::show)
                        .addOnFailureListener(e -> toast("Error: " + e.getMessage()));
                break;
            default:
                toast("Pick a list type first");
        }
    }
    /*
      Binds the list to the recycler and shows a tiny toast with count.
      If your adapter still uses submit or swap updateData for submit*/
    private void show(List<String> ids) {
        adapter.updateData(ids);  //use submit(ids) if didnt change the adapter
        toast("Loaded " + (ids == null ? 0 : ids.size()));
    }
    // tiny convenience wrapper so I dont repeat Toast.makeText everywhere
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
