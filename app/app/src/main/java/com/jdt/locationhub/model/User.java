package com.jdt.locationhub.model;

import androidx.annotation.Nullable;

public class User {
    private final String username;
    private double distance; //Km
    private final boolean privacy;
    private Position position;

    public User(String username, double distance, boolean privacy, Position position) {
        this.username = username;
        this.distance = distance;
        this.privacy = privacy;
        this.position = position;
    }

    public User(String username, double distance, boolean privacy) {
        this.username = username;
        this.distance = distance;
        this.privacy = privacy;
    }

    public String getUsername() {
        return username;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public boolean isPrivate() {
        return privacy;
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

