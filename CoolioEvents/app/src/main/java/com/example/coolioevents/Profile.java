package com.example.coolioevents;
/**
 * This is a class that defines an a user's profile.
 */
public class Profile {
    private String user_id;
    private String username;
    private String name;
    private String email;

    public Profile(String user_id, String username, String name, String email) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.email = email;
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



}
