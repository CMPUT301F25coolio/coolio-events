package com.example.coolioevents;

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

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }



}
