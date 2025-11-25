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
 * Adapter showing two columns:
 *  - Entrant ID
 *  - "Yes" or "No" under Registered column
 */
public class EntrantStatusAdapter extends RecyclerView.Adapter<EntrantStatusAdapter.VH> {

    private final List<String> entrantIds = new ArrayList<>();
    private final List<String> registeredValues = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(String entrantId, boolean isRegistered);
    }

    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void update(List<String> ids, boolean registeredYes) {
        entrantIds.clear();
        registeredValues.clear();

        if (ids != null) {
            for (String id : ids) {
                entrantIds.add(id);
                registeredValues.add(registeredYes ? "Yes" : "No");
            }
        }
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
        String registerStatus = registeredValues.get(position);

        holder.uidText.setText(entrantIds.get(position));
        holder.statusText.setText(registeredValues.get(position));

        boolean isRegistered = registerStatus.equals("Yes");

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
