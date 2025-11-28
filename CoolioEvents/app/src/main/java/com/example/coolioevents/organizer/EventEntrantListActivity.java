package com.example.coolioevents.organizer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coolioevents.R;
import com.example.coolioevents.repo.EntrantsRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Copyright 2025 Parth Mittal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * PURPOSE:
 * This screen allows an organizer to view all entrant lists for a selected event.
 * It supports four list types: Enrolled, Cancelled, Chosen, and Waitlist. Each list
 * is loaded from Firestore through EntrantsRepository and displayed using
 * EntrantStatusAdapter. The layout can optionally show a "Registered" column depending
 * on which list is being viewed (Enrolled = Yes, Chosen = No, others hidden).
 *
 * The activity also handles CSV exporting for the final enrolled entrant list.
 * A button becomes visible only when the user is viewing enrolled entrants, and
 * pressing it generates a real .csv file that can be shared or saved.
 *
 * RATIONALE:
 * Keeping list display, removal actions, and CSV exporting inside one activity made
 * event-management features easier to access for the organizer. The design keeps UI
 * and Firestore logic separate by using EntrantsRepository and an adapter, which made
 * development cleaner and easier to modify later.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-27
 */
/*
 * Displays entrant lists for an event:
 *   Enrolled, Cancelled, Chosen, Waitlist
 * Registered column only appears for Eligible states
 *   Enrolled -> Yes
 *   Chosen   -> No (selected but not registered yet)
 *
 * Also supports exporting the final ENROLLED entrant list as a CSV file*/
public class EventEntrantListActivity extends AppCompatActivity {
    public static final String EXTRA_TYPE = "listType";
    public static final int TYPE_ENROLLED  = 0;
    public static final int TYPE_CANCELLED = 1;
    public static final int TYPE_CHOSEN    = 2;
    public static final int TYPE_WAIT      = 3;
    private EntrantStatusAdapter adapter;
    private EntrantsRepository repo;
    private String eventId;
    private int currentType;
    // Whatever is currently shown in the list (usernames / IDs) so we can export it.
    private final List<String> currentEntrants = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_entrant_list);
        eventId = getIntent().getStringExtra(ListScreenActivity.EXTRA_EVENT_ID);
        currentType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_ENROLLED);
        FrameLayout back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.titleText);
        title.setText(titleFor(currentType));
        boolean showRegistered = (currentType == TYPE_CHOSEN);
        TextView headerRegistered = findViewById(R.id.headerRegistered);
        headerRegistered.setVisibility(showRegistered ? TextView.VISIBLE : TextView.GONE);
        RecyclerView rv = findViewById(R.id.recyclerEntrants);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EntrantStatusAdapter();
        adapter.setShowRegisteredColumn(showRegistered);
        rv.setAdapter(adapter);

        repo = new EntrantsRepository();
        load(eventId, currentType);
        adapter.setOnItemClickListener((entrantId, isRegistered) -> {
            if (!isRegistered) {
                showCancelDialog(entrantId);
            }
        });

        // CSV export button only visible for Enrolled list
        Button exportButton = findViewById(R.id.exportCsvButton);
        if (currentType == TYPE_ENROLLED) {
            exportButton.setVisibility(View.VISIBLE);
            exportButton.setOnClickListener(v -> exportCsv());
        } else {
            exportButton.setVisibility(View.GONE);
        }
    }
    private void load(String eventId, int type) {
        if (eventId == null || eventId.isEmpty()) {
            toast("Missing eventId");
            return;
        }
        switch (type) {
            case TYPE_ENROLLED:
                repo.getFinalEnrolled(eventId)
                        .addOnSuccessListener(ids -> bind(ids, true))
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;
            case TYPE_CANCELLED:
                repo.getCancelled(eventId)
                        .addOnSuccessListener(ids -> bind(ids, false))
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;
            case TYPE_CHOSEN:
                repo.getChosen(eventId)
                        .addOnSuccessListener(ids -> bind(ids, false)) // chosen = not registered yet
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;
            case TYPE_WAIT:
                repo.getWaitlist(eventId)
                        .addOnSuccessListener(ids -> bind(ids, false))
                        .addOnFailureListener(e -> toast(e.getMessage()));
                break;
        }
    }
    private void bind(List<String> ids, boolean registeredYes) {
        currentEntrants.clear();
        if (ids != null) {
            currentEntrants.addAll(ids);
        }
        adapter.update(ids, registeredYes);
    }
    private String titleFor(int type) {
        switch (type) {
            case TYPE_ENROLLED:
                return "Enrolled Entrant List";
            case TYPE_CANCELLED:
                return "Cancelled Entrant List";
            case TYPE_CHOSEN:
                return "Chosen Entrant List";
            case TYPE_WAIT:
                return "Wait List";
        }
        return "Entrants List";
    }
    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    private void showCancelDialog(String entrantId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Entrant");
        builder.setMessage("Do you want to remove \"" + entrantId + "\" from this event?");
        builder.setPositiveButton("DELETE", (dialog, which) -> {
            repo.removeEntrant(eventId, entrantId)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Entrant removed", Toast.LENGTH_SHORT).show();
                        load(eventId, currentType);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    /**
     * Creates a real .csv file in cache and shares it.
     */
    private void exportCsv() {
        if (currentType != TYPE_ENROLLED) {
            toast("CSV export is only available for enrolled entrants.");
            return;
        }

        if (currentEntrants.isEmpty()) {
            toast("No enrolled entrants to export.");
            return;
        }
        // Build CSV content
        StringBuilder sb = new StringBuilder();
        sb.append("Entrant\n");
        for (String name : currentEntrants) {
            if (name == null) {
                name = "";
            }
            String escaped = name.replace("\"", "\"\"");
            sb.append("\"").append(escaped).append("\"").append("\n");
        }
        String csv = sb.toString();

        // Write to a temp file in cacheDir
        File csvFile = new File(getCacheDir(), "enrolled_entrants.csv");
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write(csv);
        } catch (IOException e) {
            toast("Failed to create CSV file: " + e.getMessage());
            return;
        }
        // Get content URI via FileProvider
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                csvFile
        );
        // Share file
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/csv");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Enrolled Entrants CSV");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(sendIntent, "Export Enrolled Entrants"));
    }
}
