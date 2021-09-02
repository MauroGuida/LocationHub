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
            Scanner s = new Scanner(message.substring(6, message.length()-1));
            s.useDelimiter("@");

            List<String> users = new ArrayList<>();
            while (s.hasNext())
                users.add(s.next());

            users.forEach(u -> {
                Scanner ss = new Scanner(u);
                ss.useDelimiter(" ");

                String username = ss.next();
                float distance = ss.nextFloat();
                int isPrivate = ss.nextInt();

                Scanner sss = new Scanner(u.substring(u.indexOf('[')+1, u.indexOf(']')));
                sss.useDelimiter(";");

                float lat = sss.nextFloat();
                float log = sss.nextFloat();
                String address = sss.next();
                String local = sss.next();
                String zipCode = sss.next();
                String country = sss.next();
                String countryCode = sss.next();

                userList.add(new User(username, distance, isPrivate == 1, new Position.Builder()
                        .latitude(lat)
                        .longitude(log)
                        .addressLine(address)
                        .locality(local)
                        .postalCode(zipCode)
                        .countryName(country)
                        .countryCode(countryCode)
                        .build()));
            });
        }

        return userList;
    }
}
