package com.jdt.locationhub.model;

import androidx.annotation.Nullable;

public class User {
    private final String username;
    private float distance; //Km
    private Position position;

    public User(String username) {
        this.username = username;
    }

    public User(String username, float distance, Position position) {
        this.username = username;
        this.distance = distance;
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String serialize() {
        return username + " " + position.serialize();
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

