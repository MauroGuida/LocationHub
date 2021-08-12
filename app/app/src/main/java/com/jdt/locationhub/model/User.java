package com.jdt.locationhub.model;

import androidx.annotation.NonNull;

public class User {
    private final String username;
    private final double latitude;
    private final double longitude;
    private final String addressLine;
    private final String locality;
    private final String postalCode;
    private final String countryName;
    private final String countryCode;

    private User(String username, double latitude, double longitude, String addressLine,
                 String locality, String postalCode, String countryName, String countryCode) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressLine = addressLine;
        this.locality = locality;
        this.postalCode = postalCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public String getUsername() {
        return username;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getLocality() {
        return locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public boolean isLocationValid() {
        return (latitude != 0 && longitude != 0) && !addressLine.isEmpty()
                //&& !locality.isEmpty()
                && !postalCode.isEmpty()
                && !countryName.isEmpty() && !countryCode.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return "UserLocation{" +
                "username=" + username +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", addressLine='" + addressLine + '\'' +
                ", locality='" + locality + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }

    public String serialize() {
        return "[" + username + ";" +
                latitude + ";" +
                longitude + ";" +
                addressLine + ";" +
                locality + ";" +
                postalCode + ";" +
                countryName + ";" +
                countryCode + "]";
    }

    public static class Builder {
        private String username;
        private double latitude;
        private double longitude;
        private String addressLine;
        private String locality;
        private String postalCode;
        private String countryName;
        private String countryCode;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder addressLine(String addressLine) {
            this.addressLine = addressLine;
            return this;
        }

        public Builder locality(String locality) {
            this.locality = locality;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder countryName(String countryName) {
            this.countryName = countryName;
            return this;
        }

        public Builder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public User build() {
            return new User(this.username, this.latitude, this.longitude, this.addressLine,
                    this.locality, this.postalCode, this.countryName, this.countryCode);
        }
    }
}

