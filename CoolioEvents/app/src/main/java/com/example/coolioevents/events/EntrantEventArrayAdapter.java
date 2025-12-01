package com.example.coolioevents.events;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coolioevents.Event;
import com.example.coolioevents.MainActivity;
import com.example.coolioevents.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Copyright 2025 Ethan Diep & Avery Dancocks & Juliane Phan
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
 * This class represents an array adapter for a single event for entrant.
 * Displays the details of an event in a list view.
 *
 * RATIONALE:
 * Used to ensure the List View has a proper representation of
 * all the events in the ArrayList.
 *
 * @author Ethan Diep & Avery Dancocks & Juliane Phan
 * @version 1.0
 * @since 2025-11-05
 */
public class EntrantEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> eventList;
    private Context context;
    private FirebaseUser currentUser;

    public EntrantEventArrayAdapter(Context context, ArrayList<Event> eventList){
        super(context,0, eventList);
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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Event event = eventList.get(position);
        ImageView eventImageView = view.findViewById(R.id.imageView);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView eventOrganizer = view.findViewById(R.id.eventOrganizer);
        TextView eventDescription = view.findViewById(R.id.eventDescription);
        TextView eventTime = view.findViewById(R.id.eventTime);
        TextView dateDay = view.findViewById(R.id.dateDay);
        TextView dateMonth = view.findViewById(R.id.dateMonth);
        TextView dateTime = view.findViewById(R.id.dateTime);
        TextView eventLocation = view.findViewById(R.id.eventLocation);
        TextView eventRegPrd = view.findViewById(R.id.eventRegPeriod);
        TextView eventMaxEntrees = view.findViewById(R.id.eventmaxEntrees);
        TextView eventStatus = view.findViewById(R.id.eventStatus);
        TextView eventUserStatus = view.findViewById(R.id.eventuserStatus);
        TextView eventUserStatusRegistration = view.findViewById(R.id.eventUserStatusRegistration);
        TextView eventWaitingCount = view.findViewById(R.id.eventWaitingCount);
        ChipGroup tagsChipGroup = view.findViewById(R.id.tagsGroup);

        eventName.setText(event.getDetails().getEventName());
        eventOrganizer.setText(String.format("%s", event.getOrganizer().getProfile().getUsername())); // Sets event organizer text
        eventDescription.setText(String.format("Description: %s", event.getDetails().getEventDescription())); // Sets event description text
        eventRegPrd.setText(String.format("Registration Period: %s", event.getDetails().getRegistrationPeriod())); // Sets event registration period text
        eventMaxEntrees.setText(String.format("Entrant Limit: %s", String.valueOf(event.getDetails().getEntrantLimit()))); // Sets entrant limit organizer text

        // Setting Certain Views Invisible for search and home fragments
        eventStatus.setVisibility(View.GONE);
        eventUserStatus.setVisibility(View.GONE);
        eventUserStatusRegistration.setVisibility(View.GONE);

        //https://stackoverflow.com/questions/45232608/how-to-load-image-into-imageview-from-url-using-glide-v4-0-0rc1
        // Set event image with Glide
        Glide.with(context)
                .load(event.getDetails().getPosterUrl()) // loads poster URL
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .fallback(R.drawable.logo) // If imageURL is null
                .into(eventImageView);

        if (event.getDetails().getEventLocation() != null){
            eventLocation.setText(String.format("%s",event.getDetails().getEventLocation())); // Sets event location if not null
        }

        // Setting Event Date
        if (event.getDetails().getEventDateTime() != null){
            Calendar calendar = Calendar.getInstance(); // Make new calender object based on date and Time
            calendar.setTime(event.getDetails().getEventDateTime());


            SimpleDateFormat monthAbrv = new SimpleDateFormat("MMM", Locale.ENGLISH); // Converts calender to 3 month format
            SimpleDateFormat dayAbrv = new SimpleDateFormat("dd", Locale.ENGLISH); // Converts calender to day format
            SimpleDateFormat timeAbrv = new SimpleDateFormat("hh:mm a", Locale.ENGLISH); // Converts calender to time format

            dateMonth.setText(monthAbrv.format(calendar.getTime()).toUpperCase());
            dateDay.setText(dayAbrv.format(calendar.getTime()));
            dateTime.setText(timeAbrv.format(calendar.getTime()));

            eventTime.setText(String.format("Time: %s",event.getDetails().getEventDateTime())); // Sets event time if not null


        }


        /*
        // Setting event status text
        if (event.getDetails().getStatus().equals("open")) {
            // If event open make text open with green background
            eventStatus.setText("Open");
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greenshapebackground));
        } else if (event.getDetails().getStatus().equals("closed")) {
            eventStatus.setText("Closed");
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.redshapebackground));
        } else {
            // If event closed make text open with red background
            eventStatus.setText(event.getDetails().getStatus());
            eventStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.redshapebackground));
        }

         */

        /*
        List<String> waitlist = event.getWaitlistEntrants();
        List<String> chosenEntrants = event.getChosenEntrants();
        List<String> acceptedEntrants = event.getAcceptedEntrants();
        String userId = currentUser.getUid();

        boolean isUserOnWaitList = waitlist.contains(userId);
        boolean isUserChosen = chosenEntrants.contains(userId);
        boolean isUserAccepted = acceptedEntrants.contains(userId);

        System.out.println(isUserOnWaitList);

        // Setting user status text
        if (isUserOnWaitList) {
            // Set Visibility of registration status
            eventUserStatusRegistration.setVisibility(View.GONE);

            // If the current user is in the waitlist, display an indicator that user is in the waiting list
            eventUserStatus.setText("In Waiting List");
            eventUserStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greenshapebackground));
        }
        if (!isUserOnWaitList && !isUserChosen && !isUserAccepted) {
            // Set Visibility of registration status
            eventUserStatusRegistration.setVisibility(View.GONE);

            // If the current user is not in the waitlist, display an indicator that user is  not in the waiting list
            eventUserStatus.setText("Not In Waiting List");
            eventUserStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greybackground));
        }
        if (isUserChosen) {
            // Set Visibility of registration status
            eventUserStatusRegistration.setVisibility(View.VISIBLE);

            // If the current user is chosen, display an indicator that user is chosen
            eventUserStatus.setText("Chosen");
            eventUserStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.greenshapebackground));
        }
        if (isUserAccepted) {
            // Set visibility of registration status
            eventUserStatusRegistration.setVisibility(View.VISIBLE);

            // Set text and colour of user status
            eventUserStatus.setText("Chosen");
            eventUserStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));

            eventUserStatusRegistration.setText("Registered");
            eventUserStatusRegistration.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.greenshapebackground));
        }

         */

        //Setting tags
        tagsChipGroup.removeAllViews();
        if (event.getDetails().getTags() != null){
            for (String tagString : event.getDetails().getTags()){
                Chip tag = new Chip(context);
                final float scale = getContext().getResources().getDisplayMetrics().density;
                tag.setText(tagString);
                tag.setChipStrokeWidth(0);
                tag.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                tag.setClickable(false);
                tagsChipGroup.addView(tag);
            }
        }
        if (tagsChipGroup.getChildCount() == 0) {
            // If there are no chips hide the entire group
            tagsChipGroup.setVisibility(View.GONE);
        } else {
            // If there are chips make sure the group is visible
            tagsChipGroup.setVisibility(View.VISIBLE);
        }

        // Set waiting list count text
        if (event.getWaitlistEntrants().size() == 1){

            eventWaitingCount.setText("People in Waitlist: 1"); //Set waiting list count
        }
        else {
            eventWaitingCount.setText(String.format("People in Waitlist: %s", String.valueOf(event.getWaitlistEntrants().size()))); //Set waiting list count
        }

        return view;
    }

}
