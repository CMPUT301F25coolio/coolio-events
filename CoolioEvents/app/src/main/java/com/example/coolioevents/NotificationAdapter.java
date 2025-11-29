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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationData> notificationList;

    public NotificationAdapter(List<NotificationData> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

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

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.notifMessage);
            time = itemView.findViewById(R.id.notifTime);
        }
    }
}