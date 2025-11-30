package com.example.coolioevents.Entrant;

import android.util.Log;

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
 * Copyright 2025 Ethan Diep & Juliane Phan
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
 * @author Ethan Diep & Juliane Phan
 * @version 1.5
 * @since 2025-11-20
 */
public class UserViewModel extends ViewModel {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Map<String, User>> userMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Organizer>> organizerMap = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Entrant>> entrantMap = new MutableLiveData<>();

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
     * Initializes a database snapshotlistener to keep userMap, entrantMap, and organizerMap up to date with the database.
     */
    private void addUserSL(){
        // Snapshot listener for users in db - updates userMap when updated in db
        db.collection("users").addSnapshotListener((value, error) ->{
            if (value !=null && !value.isEmpty()){
                userMap.setValue(new HashMap<>()); // Make userMap empty
                Map<String, User> newUserMap = userMap.getValue(); // Placeholder userMap which will be assigned to userMap later

                Map<String, Entrant> newEntrantMap = new HashMap<>();
                Map<String, Organizer> newOrganizerMap = new HashMap<>();

                for (QueryDocumentSnapshot snapshot : value){
                    String userID = snapshot.getId();
                    String username = snapshot.getString("username");
                    String name = snapshot.getString("name");
                    String email = snapshot.getString("email");
                    String role = snapshot.getString("role");

                    // Update userMap
                    User user = new User();
                    user.setProfile(new Profile(userID,username,name,email));
                    newUserMap.put(userID, user);
                    System.out.println("hello");

                    // Update entrantMap
                    if (role.equals("Entrant")) {
                        Entrant entrant = new Entrant(new Profile(userID,username,name,email));
                        newEntrantMap.put(userID, entrant);
                    }

                    // Update organizerMap
                    if (role.equals("Organizer")) {
                        Organizer organizer = new Organizer(new Profile(userID,username,name,email));
                        //organizer.setProfile(new Profile(userID,username,name,email));
                        newOrganizerMap.put(userID, organizer);
                    }
                }
                userMap.setValue(newUserMap); // Sets userMap to updated userMap
                entrantMap.setValue(newEntrantMap);  // Sets entrantMap to updated entrantMap
                organizerMap.setValue(newOrganizerMap);  // Sets organizerMap to updated organizerMap
            }
        });
    }

    /**
     * Returns a map of all users in the database
     * @return The map of all users in the database
     */
    public MutableLiveData<Map<String, User>> getUserMap(){
        return userMap;
    }

    /**
     * Returns a map of all entrants in the database
     * @return The map of all entrants in the database
     */
    public MutableLiveData<Map<String, Entrant>> getEntrantMap(){
        return entrantMap;
    }

    /**
     * Returns a map of all organizers in the database
     * @return The map of all organizers in the database
     */
    public MutableLiveData<Map<String, Organizer>> getOrganizerMap() {
        return organizerMap;
    }


    /**
     * This is called when an administrator chooses to delete a user (entrant/organizer).
     * Deletes the specific user from the firebase
     *
     * @param userId
     *      event that the administrator wishes to delete
     */
    public void deleteUser(String userId) {
        if (userId == null) {
            return;
        }

        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ViewModel", "SUCCESS: User " + userId + " deleted");
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewModel", "FAILURE: Could not delete user " + userId, e);
                });
    }

}
