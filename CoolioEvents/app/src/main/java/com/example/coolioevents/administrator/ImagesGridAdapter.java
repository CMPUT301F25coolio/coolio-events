package com.example.coolioevents.administrator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coolioevents.R;
import com.example.coolioevents.events.EventImageData;

import java.util.ArrayList;

//https://developer.android.com/develop/ui/views/layout/recyclerview
public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter.ViewHolder>{
    private final ArrayList<EventImageData> imageList;
    private final Context context;

    public ImagesGridAdapter(ArrayList<EventImageData> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView eventImageView;
        final TextView organizerUsername;

        public ViewHolder(View view) {
            super(view);
            eventImageView = view.findViewById(R.id.event_image);
            organizerUsername = view.findViewById(R.id.organizer_username);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.administrator_image_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        EventImageData data = imageList.get(position);
        String imageUrl = data.getEventPoster();

        // ======================== DEBUG LOGGING ========================
        // This will print the data to the Logcat window.
        // The tag "AdapterDebug" lets us filter for this specific message.
        Log.d("AdapterDebug", "Position: " + position + " | Username: " + data.getOrganizerUsername() + " | URL: " + imageUrl);
        // ===============================================================

        // Set organizer username
        viewHolder.organizerUsername.setText(data.getOrganizerUsername());

        // Set image with Glide
        //https://stackoverflow.com/questions/45232608/how-to-load-image-into-imageview-from-url-using-glide-v4-0-0rc1

        Glide.with(context)
                .load(data.getEventPoster()) // loads poster URL
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .fallback(R.drawable.ic_image_placeholder) // If imageURL is null
                .into(viewHolder.eventImageView);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
