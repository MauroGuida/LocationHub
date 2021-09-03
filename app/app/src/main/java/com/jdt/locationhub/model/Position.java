package com.jdt.locationhub.model;

import java.util.Objects;

public class Position {
    private final double latitude;
    private final double longitude;
    private final String addressLine;
    private final String locality;
    private final String postalCode;
    private final String countryName;
    private final String countryCode;

    private Position(double latitude, double longitude, String addressLine, String locality,
                     String postalCode, String countryName, String countryCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressLine = addressLine;
        this.locality = locality;
        this.postalCode = postalCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
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

    public String serialize() {
        return  "[" + latitude + ";" +
                longitude + ";" +
                addressLine + ";" +
                locality + ";" +
                postalCode + ";" +
                countryName + ";" +
                countryCode + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        return Double.compare(position.latitude, latitude) == 0 && Double.compare(position.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    public static class Builder {
        private double latitude;
        private double longitude;
        private String addressLine;
        private String locality;
        private String postalCode;
        private String countryName;
        private String countryCode;

        public Position.Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Position.Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Position.Builder addressLine(String addressLine) {
            this.addressLine = addressLine;
            return this;
        }

        public Position.Builder locality(String locality) {
            this.locality = locality;
            return this;
        }

        public Position.Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Position.Builder countryName(String countryName) {
            this.countryName = countryName;
            return this;
        }

        public Position.Builder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public Position build() {
            return new Position(this.latitude, this.longitude, this.addressLine, this.locality,
                    this.postalCode, this.countryName, this.countryCode);
        }
    }
}
