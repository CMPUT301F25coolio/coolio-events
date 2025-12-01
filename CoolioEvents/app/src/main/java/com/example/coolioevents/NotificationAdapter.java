package com.example.coolioevents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * Adapter for displaying notifications inside a RecyclerView.
 *
 * Responsibilities:
 *  • Bind each NotificationData item to the corresponding layout.
 *  • Format timestamps into a readable form.
 *  • Show a blue dot for unread notifications.
 *
 * The adapter expects a pre-populated list of NotificationData objects.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationData> notificationList;
    /**
     * Creates a new adapter with the given list of notifications.
     *
     * @param notificationList List of NotificationData items to display.
     */
    public NotificationAdapter(List<NotificationData> notificationList) {
        this.notificationList = notificationList;
    }
    /**
     * Inflates the item layout for each notification row.
     *
     * @param parent Parent ViewGroup.
     * @param viewType Unused view type.
     * @return A ViewHolder containing the inflated layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }
    /**
     * Binds a single notification to the ViewHolder.
     *
     * Behavior:
     *  • Converts the Firestore timestamp into a readable format (e.g., “Jan 4 3:15pm”)
     *  • Displays the notification message.
     *  • Shows a blue dot if the notification is unread.
     *
     * @param holder ViewHolder for the item.
     * @param position Index of the current notification.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationData notif = notificationList.get(position);
        Date notificationDate = notif.getCreatedAt();

        String pattern = "MMM d h:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        String formattedTime = sdf.format(notificationDate);
        formattedTime = formattedTime.replace("AM", "am").replace("PM", "pm");
        holder.time.setText(formattedTime);
        holder.message.setText(notif.getMessage());

        if (!notif.isShown()) {
            holder.blueDot.setVisibility(View.VISIBLE);
        } else {
            holder.blueDot.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the number of notifications to display.
     *
     * @return List size of notifications.
     */
    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    /**
     * Holds references to the views inside each notification row.
     * Includes:
     *  • message – Actual notification text.
     *  • time – Formatted timestamp.
     *  • blueDot – Visibility indicates unread state.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, time;
        View blueDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.notifMessage);
            time = itemView.findViewById(R.id.notifTime);
            blueDot = itemView.findViewById(R.id.blue_dot);
        }
    }
}