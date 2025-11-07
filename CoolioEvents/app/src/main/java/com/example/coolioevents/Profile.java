package com.example.coolioevents;
/**
 * Copyright 2025 Aasta Tsai
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
 * This class defines a profile.
 * It contains all the attributes of a profile.
 * Profiles are had by Entrants and Organizers.
 *
 * @author Aasta Tsai
 * @version 1.0
 * @since 2025-11-05
 */
public class Profile {
    private String user_id;
    private String username;
    private String name;
    private String email;


    /**
     * This is a constructor to make a profile with parameters
     * @param user_id
     *      user ID
     * @param username
     *      Username
     * @param name
     *      Name of user
     * @param email
     *      email of user
     */
    public Profile(String user_id, String username, String name, String email) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.email = email;
    }


    /**
     * This is a constructor to make a profile without parameters
     */
    public Profile(){


    }


    /**
     * This gets the user ID of the user
     * @return
     *      Returns the user's user_id
     */
    public String getUser_id() {
        return user_id;
    }


    /**
     * This gets the user's  username
     * @return
     *      Returns the user's username
     */
    public String getUsername() {
        return username;
    }


    /**
     * This gets the user's full name
     * @return
     *      Returns the user's full name
     */
    public String getName() {
        return name;
    }


    /**
     * This gets the user's email
     * @return
     *      Returns the user's email
     */
    public String getEmail() {
        return email;
    }


    /**
     * This sets a users' id
     * @param user_id
     *      the user id to set
     */
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }


    /**
     * This sets a users' username
     * @param username
     *      the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * This sets a users' name
     * @param name
     *      the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * This sets a users' email
     * @param email
     *      the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
