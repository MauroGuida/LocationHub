package com.jdt.locationhub.tool;

import com.jdt.locationhub.model.Position;
import com.jdt.locationhub.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StringParser {
    public static List<User> usersParser(String message) {
        List<User> userList = new ArrayList<>();

        if (message != null && !message.isEmpty() && !message.equals("OK - {}")) {
            List<String> users = userStringListExtractor(message.substring(6, message.length()-1));

            users.forEach(stringUser -> {
                User user = userExtractor(stringUser);
                Position position = positionExtractor(stringUser.substring(stringUser.indexOf('[')+1, stringUser.indexOf(']')));

                user.setPosition(position);

                userList.add(user);
            });
        }

        return userList;
    }

    private static List<String> userStringListExtractor(String msg) {
        Scanner strScanner = new Scanner(msg);
        strScanner.useDelimiter("@");

        List<String> users = new ArrayList<>();
        while (strScanner.hasNext())
            users.add(strScanner.next());

        return users;
    }

    private static User userExtractor(String msg) {
        Scanner strScanner = new Scanner(msg);
        strScanner.useDelimiter(" ");

        String username = strScanner.next();
        double distance = strScanner.nextDouble();
        int isPrivate = strScanner.nextInt();

        return new User(username, distance, isPrivate == 1);
    }

    private static Position positionExtractor(String msg) {
        Scanner strScanner = new Scanner(msg);
        strScanner.useDelimiter(";");

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
    }
}
