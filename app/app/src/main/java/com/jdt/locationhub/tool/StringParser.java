package com.jdt.locationhub.tool;

import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StringParser {
    private static final Locale LOCAL_TYPE = Locale.UK;

    public static List<User> usersParser(String message) {
        List<User> userList = new ArrayList<>();

        if (message != null && !message.isEmpty() && !message.equals("OK - {}")) {
            List<String> users = userStringListExtractor(message.substring(6, message.length()-1));

            users.forEach(stringUser -> {
                User user = userExtractor(stringUser);
                Position position = positionExtractor(stringUser.substring(stringUser.indexOf('[')+1, stringUser.indexOf(']')));

                if (user != null && position != null) {
                    user.setPosition(position);
                    userList.add(user);
                }
            });
        }

        return userList;
    }

    private static List<String> userStringListExtractor(String msg) {
        Scanner strScanner = new Scanner(msg);
        strScanner.useLocale(LOCAL_TYPE);
        strScanner.useDelimiter("@");

        List<String> users = new ArrayList<>();
        while (strScanner.hasNext())
            users.add(strScanner.next());

        return users;
    }

    private static User userExtractor(String msg) {
        Scanner strScanner = new Scanner(msg);
        strScanner.useLocale(LOCAL_TYPE);
        strScanner.useDelimiter(" ");

        try {
            String username = strScanner.next();
            double distance = strScanner.nextDouble();
            int isPrivate = strScanner.nextInt();

            return new User(username, distance, isPrivate == 1);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private static Position positionExtractor(String msg) {
        Scanner strScanner = new Scanner(msg);
        strScanner.useLocale(LOCAL_TYPE);
        strScanner.useDelimiter(";");

        try {
            double lat = strScanner.nextDouble();
            double log = strScanner.nextDouble();
            String address = strScanner.next();
            String locality = strScanner.next();
            String zipCode = strScanner.next();
            String countryName = strScanner.next();
            String countryCode = strScanner.next();

            return new Position.Builder()
                    .latitude(lat)
                    .longitude(log)
                    .addressLine(address)
                    .locality(locality)
                    .postalCode(zipCode)
                    .countryName(countryName)
                    .countryCode(countryCode)
                    .build();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
