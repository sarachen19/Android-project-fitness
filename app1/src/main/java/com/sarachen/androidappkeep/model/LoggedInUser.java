package com.sarachen.androidappkeep.model;

import org.parceler.Parcel;

import java.util.HashSet;
import java.util.Set;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Parcel
public class LoggedInUser {

    String username, password, email, id;
    Set<Integer> courses;
    public LoggedInUser() {}
    public LoggedInUser(String username, String password, String email, String id, Set<Integer> courses) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.id = id;
        this.courses = courses;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Set<Integer> getCourses() {
        return courses;
    }

    public String getUserId() {
        return id;
    }

    public String getDisplayName() {
        return username;
    }
}