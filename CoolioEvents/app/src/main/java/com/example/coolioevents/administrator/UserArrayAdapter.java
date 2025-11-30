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

import com.example.coolioevents.R;
import com.example.coolioevents.User;

import java.util.ArrayList;

/**
 * Copyright 2025 Avery Dancocks
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
 * This class is an array adapter for both the entrant and organizer
 * list views that the administrator has. It displays the user's
 * username.
 *
 * RATIONALE:
 * Organizers and entrants both extend user so a single array adapter
 * was designed to display both types of users and to reduce code redundancy.
 *
 * @author Avery Dancocks
 * @version 1.0
 * @since 2025-11-19
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> userList;
    private Context context;
    public UserArrayAdapter(Context context, ArrayList<User> userList){
        super(context,0, userList);
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.profile_content, parent, false);
        }

        User user = userList.get(position);
        // Get Views
        TextView userName = view.findViewById(R.id.profile_name);
        ImageView profileCircle = view.findViewById(R.id.profile_circle);
        TextView profileText = view.findViewById(R.id.icon_text);
        // Set View
        userName.setText(user.getProfile().getUsername());

        // Setting Profile Colour
        int colourId = getColour(user.getProfile().getUser_id());
        int userColour = ContextCompat.getColor(getContext(), colourId);
        profileCircle.getBackground().setTint(userColour);

        String name = user.getProfile().getName();
        if (name != null) {
            String initials = getInitials(name);
            profileText.setText(initials);
        }

        return view;
    }

    /**
     * This function uses hashing to return a colour based on a
     * user's ID. The function will return the same colour
     * for the same user ID every time.
     * @param userId
     *      The user ID that is to be hashed
     * @return
     *      an integer representing a colour
     */
    private int getColour(String userId) {
        int[] colourPalette = new int[]{
                R.color.medium_purple,
                R.color.medium_green,
                R.color.medium_blue,
                R.color.medium_yellow,
        };

        int hash = userId.hashCode();

        int index = Math.abs(hash % colourPalette.length);

        return colourPalette[index];
    }

    /**
     * This method returns a string of initials or a single initial
     * based on the provided string name.
     * @param name
     *      String name from which the initials will be obtained
     */
    private String getInitials(String name) {
        String[] words = name.split(" ");
        int size = words.length;

        if (size == 1) {
            // If there is one whole name just return the first initial
            String firstWord = words[0];
            char firstLetter = firstWord.charAt(0);
            return String.valueOf(firstLetter);
        }
        else if (size >= 2) {
            // Regardless of how many other words are in the name take the first
            // two words and get their first letters
            String firstWord = words[0];
            String secondWord = words[1];
            char firstLetter = firstWord.charAt(0);
            char secondLetter = secondWord.charAt(0);
            return "" + firstLetter + secondLetter;
        }
        return ""; // If no words are found
    }
}
