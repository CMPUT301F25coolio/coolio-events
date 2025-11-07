package com.example.coolioevents.organizer;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
 * A basic RecyclerView adapter that displays entrant IDs in a simple list format.
 * Used by OrganizerEntrantsActivity to show waitlist, chosen, and final entrants.
 *
 * RATIONALE:
 * The adapter is kept lightweight because the entrant lists are short and do not
 * require custom view holders or performance optimizations. Simplicity helps with
 * readability and quick debugging.
 *
 * OUTSTANDING ISSUES:
 * Currently shows only plain text. Could later include profile details or click actions.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-07
 */
/*
  Simple RecyclerView adapter that just lists entrant IDs as plain text.
  Used in OrganizerEntrantsActivity for showing waitlist/chosen/final lists
  Note nothing fancy here, just basic binding so its easier to test.*/
public class EntrantIDAdapter extends RecyclerView.Adapter<EntrantIDAdapter.ViewHolder> {
    // keeps the list of entrant IDs usually Firestore document uids
    private final List<String> entrantIds = new ArrayList<>();
    // replaces current data with a new list and refreshes the recycler
    public void updateData(List<String> newIds) {
        entrantIds.clear();
        if (newIds != null) {
            entrantIds.addAll(newIds);
        }
        // refresh everything fine since lists are short
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // using built in simplelistitem1 for speed
        TextView rowText = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(rowText);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // sets each TextView to show one entrant ID
        holder.idText.setText(entrantIds.get(position));
    }
    @Override
    public int getItemCount() {
        return entrantIds.size();
    }
    // basic holder class that just wraps a TextView
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView idText;
        ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            idText = itemView;
        }
    }
}
