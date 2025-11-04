package com.example.coolioevents.organizer;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
/*
  Simple RecyclerView adapter that just lists entrant IDs as plain text.
  Used in OrganizerEntrantsActivity for showing waitlist/chosen/final lists
  Note nothing fancy here, just basic binding so its easier to test.*/
public class EntrantIdAdapter extends RecyclerView.Adapter<EntrantIdAdapter.ViewHolder> {
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
