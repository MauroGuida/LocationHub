package com.jdt.locationhub.model;

import androidx.annotation.Nullable;

public class User {
    private final String username;
    private Position position;

    public User(String username) {
        this.username = username;
    }

    public User(String username, Position position) {
        this.username = username;
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        if (!(obj instanceof User)) return false;

        User u = (User) obj;

        return username.equals(u.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}

