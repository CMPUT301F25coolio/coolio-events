package com.example.coolioevents.organizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coolioevents.R;
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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This adapter is used on the final entrant list screen the yes/no list.
 * It takes a list of small row objects (entrantId and enrolled flag) and
 * displays them inside a RecyclerView.
 *
 * RATIONALE:
 * Instead of manually creating views every time, RecyclerView lets us reuse
 * row layouts efficiently. This adapter basically converts each row model
 * from EventEntrantListActivity into something that can appear on the screen.
 *
 * HOW IT WORKS:
 *  submitList() refreshes the internal list and updates UI
 *  onCreateViewHolder() inflates the row layout XML
 *  onBindViewHolder() fills each row with the correct data
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-16
 */
public class EventEntrantListAdapter
        extends RecyclerView.Adapter<EventEntrantListAdapter.ViewHolder> {
    // This list stores the rows that we need to show in the RecyclerView
    private final List<EventEntrantListActivity.EventEntrantRow> rows = new ArrayList<>();
    /*Replaces the current list of rows with the new result from Firestore
      RecyclerView doesnt automatically know things changed, so we call
      notifyDataSetChanged() to redraw*/
    public void submitList(List<EventEntrantListActivity.EventEntrantRow> newRows) {
        rows.clear(); // wipe previous data so we dont mix lists
        if (newRows != null) {
            rows.addAll(newRows); // add the latest results
        }
        notifyDataSetChanged(); // tells RecyclerView to refresh
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        // Inflate the row XML file item_event_entrant_row.xml
        // This basically creates the UI for a single row.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_entrant_row, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        // Grab the row model at this spot
        EventEntrantListActivity.EventEntrantRow row = rows.get(position);
        // Put the entrant ID text in the first column
        holder.nameText.setText(row.entrantId);
        // Show Yes if they accept, otherwise No
        holder.registeredText.setText(row.registered ? "Yes" : "No");
    }
    @Override
    public int getItemCount() {
        // RecyclerView asks how many rows to display
        return rows.size();
    }
    /*ViewHolder is like a mini controller for one row
      It stores references to the TextViews so RecyclerView
      doesnt need to call findViewById repeatedly*/
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;        // left column user ID
        final TextView registeredText;  // right column Yes or No
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // linking the two text fields with their XML IDs
            nameText = itemView.findViewById(R.id.text_row_entrant_name);
            registeredText = itemView.findViewById(R.id.text_row_registered);
        }
    }
}
