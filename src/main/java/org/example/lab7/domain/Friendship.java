package org.example.lab7.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the friendship between 2 users
 */
public class Friendship extends Entity<StringPair>{
    LocalDateTime friendsFrom;
    /**
     * Constructor, keeps the IDs sorted alphabetically
     * @param first, String, the first ID
     * @param second, String, the second ID
     */
    public Friendship(String first, String second) {
        super.setId(new StringPair(first, second));
        this.friendsFrom = LocalDateTime.now();
    }
    public Friendship(String first, String second, LocalDateTime friendsFrom) {
        super.setId(new StringPair(first, second));
        this.friendsFrom = friendsFrom;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {
        return  getId().getFirst() + " , " + getId().getSecond() + " , " + this.friendsFrom.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
