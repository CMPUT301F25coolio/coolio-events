package com.example.coolioevents.administrator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coolioevents.R;

/**
 * Copyright 2025 Juliane Phan
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
 * This class represents a fragment for a specific notification log.
 * It is displayed when an Administrator clicks on a specific notification log in the
 * Notifications screen.
 * It displays the notification's details.
 *
 * @author Juliane Phan
 * @version 1.0
 * @since 2025-11-30
 */

public class AdministratorNotificationFragment extends Fragment {
    private String dateString;
    private String timeString;
    private String senderString;
    private String receiverString;
    private String typeString;
    private String messageString;

    // UI components
    private TextView dateView;
    private TextView timeView;
    private TextView senderView;
    private TextView receiverView;
    private TextView typeView;
    private TextView messageView;
    private Button closeButton;


    /**
     * This is a constructor for the notification details fragment
     *
     * @param dateString
     *      the date of the notification
     * @param timeString
     *      the time of the notification
     * @param senderString
     *      the sender of the notification
     * @param receiverString
     *      the receiver of the notification
     * @param typeString
     *      the type of the notification
     * @param messageString
     *      the message of the notification
     * @return the fragment
     */
    public static AdministratorNotificationFragment newInstance(String dateString, String timeString, String senderString, String receiverString, String typeString, String messageString) {
        AdministratorNotificationFragment fragment = new AdministratorNotificationFragment();
        Bundle args = new Bundle();
        args.putString("date", dateString);
        args.putString("time", timeString);
        args.putString("sender", senderString);
        args.putString("receiver", receiverString);
        args.putString("type", typeString);
        args.putString("message", messageString);
        fragment.setArguments(args); // Attach the bundle to the fragment
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateString = getArguments().getString("date");
        timeString = getArguments().getString("time");
        senderString = getArguments().getString("sender");
        receiverString = getArguments().getString("receiver");
        typeString = getArguments().getString("type");
        messageString = getArguments().getString("message");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentNotificationLayout = inflater.inflate(R.layout.fragment_administrator_notification_details, container, false);
        return fragmentNotificationLayout;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Establishing UI components for the notification's details
        dateView = view.findViewById(R.id.date);
        timeView = view.findViewById(R.id.time);
        senderView = view.findViewById(R.id.sender);
        receiverView = view.findViewById(R.id.receiver);
        typeView = view.findViewById(R.id.type);
        messageView = view.findViewById(R.id.message);

        // Set text for the notification's date
        dateView.setText(dateString);

        // Set text for the notification's time
        timeView.setText(timeString);

        // Set text for the notification's sender
        if (senderString != null) {
            senderView.setText(senderString);
        }
        else{
            senderView.setText("Unknown");
        }

        // Set text for the notification's receiver
        if (receiverString != null) {
            receiverView.setText(receiverString);
        }
        else{
            receiverView.setText("Unknown");
        }

        // Set text for the notification's type
        if (typeString.equals("entrantChosen")) {
            typeView.setText("Chosen");
        }
        if (typeString.equals("organizerLotteryDone")) {
            typeView.setText("Lottery Done");
        }
        if (typeString.equals("entrantNotChosen")) {
            typeView.setText("Not Chosen");
        }
        if (typeString.equals("organizerToWaitlistEntrants")) {
            typeView.setText("WL Entrant");
        }
        if (typeString.equals("organizerToChosenEntrants")) {
            typeView.setText("Chosen Entrant");
        }
        if (typeString.equals("organizerToCancelledEntrants")) {
            typeView.setText("Cancelled Entrant");
        }
        if (typeString.equals("organizerToAcceptedEntrants")) {
            typeView.setText("Accepted Entrant");
        }

        // Set text for the notification's message
        messageView.setText(messageString);

        // Establishing views needed for closing the fragment
        closeButton = view.findViewById((R.id.close_button));
        FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);
        View header = getActivity().findViewById(R.id.header);
        View columnLayout = getActivity().findViewById(R.id.column_layout);
        ListView notificationsListView = getActivity().findViewById(R.id.notifications_list_view);


        // Close button onclick activity --> Goes back to Notifications screen
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();  // Go back to Entrants/Organizers screen

                // Remove white background from fragment container
                fragmentContainer.setBackgroundColor(Color.TRANSPARENT);

                // Make ListView, header, and column layout visible
                header.setVisibility(View.VISIBLE);
                columnLayout.setVisibility(View.VISIBLE);
                notificationsListView.setVisibility(View.VISIBLE);
            }
        });
    }
}
