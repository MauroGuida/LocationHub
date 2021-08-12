package com.jdt.locationhub.model;

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
}

