package org.example.lab7.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The Node of the community graph, represents a User and all of their friends
 */
public class CommunityNode {
    private Optional<User> user;
    private Map<String, Optional<User>> friends;

    /**
     * Constructor
     * @param user, The User of the node
     */
    public CommunityNode(Optional<User> user) {
        this.user = user;
        friends = new HashMap<>();
    }

    public Optional<User> getUser() {
        return user;
    }

    /**
     * Adds a friend to the friends list of the Node
     * @param userFriend, another User, friend with this.User
     */
    public void addFriend(Optional<User> userFriend) {
        friends.put(userFriend.get().getId(), userFriend);
    }

    /**
     * @return The list of Users who are friends with this.User
     */
    public Collection<Optional<User>> getFriends() {
        return friends.values();
    }
}
