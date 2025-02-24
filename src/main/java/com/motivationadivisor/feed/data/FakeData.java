package com.motivationadivisor.feed.data;

import java.util.ArrayList;
import java.util.List;

class User {

    String username;
    String firstName;
    String lastName;
    List<String> friends;

    public User(String username, String firstName, String lastName, List<String> friends) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = friends;
    }
}


public class FakeData {

    private static final List<User> users = new ArrayList<>();
    static {
        users.add(new User("lbarnes", "Luke", "Barnes", List.of("abarnes")));
        users.add(new User("abarnes", "Alison", "Barnes", List.of("lbarnes")));
    }

    public static List<String> getUserFriendsForUser(String friendId) {
        return users.stream().filter( u -> u.username.equals(friendId)).map( user -> user.friends).flatMap(List::stream).distinct().toList();
    }

}
