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

/**
 * Copyright 2025 Avery Dancocks & Juliane Phan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This class is a grid adapter for the posters to be displayed to the
 * administrator.
 *
 * RATIONALE:
 * Posters are displayed in a grid with two columns for better user interface.
 *
 * @author Avery Dancocks & Juliane Phan
 * @version 1.0
 * @since 2025-11-19
 */
/*
Taken From: https://developer.android.com/develop/ui/views/layout/recyclerview
    License: http://www.apache.org/licenses/LICENSE-2.0
    Authored by: Android Developers
    Taken by: Avery Dancocks
    Taken on: 11/18/25
*/

/*Taken from: Google Gemini
    Prompt: How to implement onclick activity for a recycler view?
    Taken by: Juliane Phan
    Taken on: 11/20/2025
*/
public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter.ViewHolder>{
    private final ArrayList<EventImageData> imageList;
    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String imageURL);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


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


        /*
        Taken From: https://stackoverflow.com/questions/45232608/how-to-load-image-into-imageview-from-url-using-glide-v4-0-0rc1
            License: https://creativecommons.org/licenses/by-sa/3.0/
            Authored by: Bharath
            Taken by: Avery Dancocks
            Taken on: 11/18/25
         */
        // Set image with Glide
        Glide.with(context)
                .load(data.getEventPoster()) // loads poster URL
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .fallback(R.drawable.logo) // If imageURL is null
                .into(viewHolder.eventImageView);

        // Set organizer username
        viewHolder.organizerUsername.setText(data.getOrganizerUsername());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(imageUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
