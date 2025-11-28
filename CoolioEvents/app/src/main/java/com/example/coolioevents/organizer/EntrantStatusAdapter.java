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
 * PURPOSE:
 * This adapter is used in EventEntrantListActivity to display event entrants
 * inside a RecyclerView. It supports all four list types (Enrolled, Chosen,
 * Cancelled, Waitlist) and can optionally show or hide the Registered
 * column based on what list the organizer is viewing.
 *
 * RATIONALE:
 * Keeping list display logic here avoids repeating UI code across
 * activities. One adapter handles every entrant type, which made it easier
 * to build and maintain the entrant list screens.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-27
 */
/*
 * Adapter showing:
 *  - Entrant ID
 *  - Optional Yes/No under Registered column
 * For Enrolled / Chosen lists we show the Registered column.
 * For Cancelled / Wait lists we hide the Registered column.*/
public class EntrantStatusAdapter extends RecyclerView.Adapter<EntrantStatusAdapter.VH> {
    private final List<String> entrantIds = new ArrayList<>();
    // One flag for the whole list: are these considered "registered"?
    private boolean registeredYes = false;
    // Controls visibility of the Registered column
    private boolean showRegisteredColumn = true;
    public interface OnItemClickListener {
        void onItemClick(String entrantId, boolean isRegistered);
    }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    /*
     * Update the data set.
     * @param ids entrant IDs to show
     * @param registeredYes true then Yes, false then No (when the column is visible)
     */
    public void update(List<String> ids, boolean registeredYes) {
        entrantIds.clear();
        if (ids != null) {
            entrantIds.addAll(ids);
        }
        this.registeredYes = registeredYes;
        notifyDataSetChanged();
    }
    // Called from EventEntrantListActivity to hide or show the Registered column.
    public void setShowRegisteredColumn(boolean show) {
        this.showRegisteredColumn = show;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant_row, parent, false);
        return new VH(item);
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String id = entrantIds.get(position);
        holder.uidText.setText(id);
        if (showRegisteredColumn) {
            holder.statusText.setVisibility(View.VISIBLE);
            holder.statusText.setText(registeredYes ? "Yes" : "No");
        } else {
            holder.statusText.setVisibility(View.GONE);
        }
        boolean isRegistered = registeredYes;
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(id, isRegistered);
            }
        });
    }
    @Override
    public int getItemCount() {
        return entrantIds.size();
    }
    static class VH extends RecyclerView.ViewHolder {
        TextView uidText, statusText;
        VH(@NonNull View itemView) {
            super(itemView);
            uidText = itemView.findViewById(R.id.uidText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}
