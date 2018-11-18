package com.utahmsd.cs6018.instyle.util;

import com.utahmsd.cs6018.instyle.db.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Random User generator class that generates random User data records
 * to populate the User database table.
 *
 * User records that are generated have a unique email, to be consistent with the unique index on
 * the email attribute of the User database table.
 */
public class UserGenerator {
    private final static String LOG_TAG = UserGenerator.class.getSimpleName();

    private static Random rand = new Random();

    private static List<String> firstNames = generateFirstNames();
    private static List<String> lastNames = generateLastNames();
    private static List<String> passwords = generatePasswords();

    public static User generateUser() {
        User user = new User();
        user.setFirstName(firstNames.get(rand.nextInt(firstNames.size())));
        user.setLastName(lastNames.get(rand.nextInt(lastNames.size())));
        user.setEmail(generateUniqueEmail());
        user.setPassword(passwords.get(rand.nextInt(passwords.size())));
//
//        Log.d(LOG_TAG, "firstName: " + user.getFirstName());
//        Log.d(LOG_TAG, "lastName: " + user.getLastName());
//        Log.d(LOG_TAG, "email: " + user.getEmail());
//        Log.d(LOG_TAG, "password: " + user.getPassword());

        return user;
    }

    public static List<User> generateUserData(int numUsers) {
        List<User> users = new ArrayList<>();
        while (users.size() < numUsers) {
            users.add(generateUser());
        }
        return users;
    }

    private static List<String> generateFirstNames() {
        return Arrays.asList("Bob", "Bill", "Jake", "Alice",
                "Alex", "James", "Helen", "Mark", "Landon",
                "Jason", "Derek", "Claire", "Brandon", "Casey",
                "Kaitlyn", "Amber", "Allison", "Jenny", "Anna",
                "Tyler", "Michael", "Jim", "Pam", "Dwight", "Angela");
    }

    private static List<String> generateLastNames() {
        return Arrays.asList("Smith", "Jensen", "Brady",
                "Jameson", "Jenson", "West",
                "Wong", "Johnson", "Johns", "Clegg",
                "Greenwood", "Green", "Whitmore", "Beck",
                "Broadhead", "Halpert", "Young", "Ferguson",
                "Wang", "Li", "Rivera");

    }

    private static String generateUniqueEmail() {
        return UUID.randomUUID().toString() + "@gmail.com";
    }

    private static List<String> generatePasswords() {
        return Arrays.asList("superstrongpassword", "1234abcd", "password", "hello", "supersecretpassword");
    }
}

