package com.example.coolioevents.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.coolioevents.Event;
import com.example.coolioevents.R;

import java.util.ArrayList;

/**
 * Copyright 2025 Juliane Phan
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
 * This class represents an array adapter for the list of events that the administrator can browse.
 *
 * RATIONALE:
 * This class was designed to ensure that the ListView (UI) contains all events from the
 * database.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-20
 */

public class AdministratorEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Context context;
    public AdministratorEventArrayAdapter(Context context, ArrayList<Event> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_content, parent,false);
        }

        Event event = eventList.get(position);
        ImageView eventPosterImageView = view.findViewById(R.id.imageView);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView eventOrganizer = view.findViewById(R.id.eventOrganizer);
        TextView eventDescription = view.findViewById(R.id.eventDescription);
        TextView eventTime = view.findViewById(R.id.eventTime);
        TextView eventLocation = view.findViewById(R.id.eventLocation);
        TextView eventRegPrd = view.findViewById(R.id.eventRegPeriod);
        TextView eventMaxEntrees = view.findViewById(R.id.eventmaxEntrees);
        TextView eventStatus = view.findViewById(R.id.eventStatus);
        TextView eventUserStatus = view.findViewById(R.id.eventuserStatus);
        TextView eventUserStatusRegistration = view.findViewById(R.id.eventUserStatusRegistration);
        TextView eventWaitingCount = view.findViewById(R.id.eventWaitingCount);

        // Making the user status views invisible since this is the Administrator's screen
        eventUserStatus.setVisibility(View.GONE);
        eventUserStatusRegistration.setVisibility(View.GONE);

        // Setting event poster
        Glide.with(this.getContext())
                .load(event.getDetails().getPosterUrl()) // loads poster URL
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .fallback(R.drawable.ic_image_placeholder) // If imageURL is null
                .into(eventPosterImageView);

        // Setting event details (excludes the user's status such as "In Waiting List", "Selected", etc. since this is the Administrator's screen)
        eventName.setText(event.getDetails().getEventName());
        eventOrganizer.setText(String.format("Posted By: %s", event.getOrganizer().getProfile().getUsername())); // Sets event organizer text
        eventDescription.setText(String.format("Description: %s", event.getDetails().getEventDescription())); // Sets event description text
        eventRegPrd.setText(String.format("Registration Period: %s", event.getDetails().getRegistrationPeriod())); // Sets event registration period text
        eventMaxEntrees.setText(String.format("Entrant Limit: %s", String.valueOf(event.getDetails().getEntrantLimit()))); // Sets entrant limit organizer text

        // Setting event location and time
        if (event.getDetails().getEventLocation() != null){
            eventLocation.setText(String.format("Event Location: %s",event.getDetails().getEventLocation())); // Sets event location if not null
        }
        if (event.getDetails().getEventDateTime() != null){
            eventTime.setText(String.format("Time: %s",event.getDetails().getEventDateTime())); // Sets event time if not null
        }

        // Setting event status text
        if (event.getDetails().getStatus().equals("open")) {
            // If event open make text open with green background
            eventStatus.setText("Open");
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greenshapebackground));
        }
        else{
            // If event closed make text open with red background
            eventStatus.setText(event.getDetails().getStatus());
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.redshapebackground));
        }

        // Set waiting list count text
        if (event.getWaitlistEntrants().size() == 1){
            eventWaitingCount.setText("1 PERSON IN WAITING LIST"); //Set waiting list count
        } else {
            eventWaitingCount.setText(String.format("%s PEOPLE IN WAITING LIST", String.valueOf(event.getWaitlistEntrants().size()))); //Set waiting list count
        }

        return view;
    }
}