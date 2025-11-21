package com.example.coolioevents.Entrant;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coolioevents.Profile;
import com.example.coolioevents.User;
import com.example.coolioevents.organizer.Organizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
/**
 * Copyright 2025 Ethan Diep
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
 * This class represents an user viewmodel, it is used to do any
 * user related queries like getting a user with a given userid.
 *
 *
 * RATIONALE:
 * This class was designed to allow users to browse events they may be
 * interested in.
 *
 * @author Ethan Diep
 * @version 1.5
 * @since 2025-11-20
 */
public class UserViewModel extends ViewModel {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Map<String, User>> userMap = new MutableLiveData<>(); // List of all events in db

    public UserViewModel(){
        addUserSL(); // Establish the user snapshot listener which updates userMap to be up to date
    }

    /**
     * Gets a user object of a user given their id. If id provided is not associated with
     * any user, return a user with unknown profile
     *
     * @param userId
     * userId of user you wish to get user object for
     *
     * @return
     * User object with given userId (if userID does not exist a user with an 'UNKNOWN' profile)
     */
    public User getUserById(String userId){
        if (userMap.getValue().containsKey(userId)){
            // If the user exists return the user
            return userMap.getValue().get(userId);
        }
        // If user doesnt exist return a user which has unknown profile
        User unknown = new User();
        unknown.setProfile(new Profile("UNKNOWN","UNKNOWN","UNKNOWN","UNKNOWN"));
        return unknown;
    }

    /**
     * Initializes a database snapshotlistener to keep userMap up to date with the database.
     */
    private void addUserSL(){
        // Snapshot listener for users in db - updates userMap when updated in db
        db.collection("users").addSnapshotListener((value, error) ->{
            if (value !=null && !value.isEmpty()){
                userMap.setValue(new HashMap<>()); // Make userMap empty
                Map<String, User> newUserMap = userMap.getValue(); // Placeholder userMap which will be assigned to userMap later
                for (QueryDocumentSnapshot snapshot : value){
                    String userID = snapshot.getId();
                    String username = snapshot.getString("username");
                    String name = snapshot.getString("name");
                    String email = snapshot.getString("email");
                    User user = new User();
                    user.setProfile(new Profile(userID,username,name,email));
                    newUserMap.put(userID, user);
                    System.out.println("hello");
                }
                userMap.setValue(newUserMap); // Sets userMap to updated userMap
            }
        });
    }

}
