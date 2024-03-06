package org.example.lab7.domain;

import java.util.Objects;

public class Conversation extends Entity<Integer> {
    private final String firstUser;
    private final String secondUser;

    public Conversation(int id_conversaton, String firstUser, String secondUser) {
        this(firstUser, secondUser);
        super.setId(id_conversaton);
    }

    public Conversation(String firstUser, String secondUser) {
        if (firstUser.compareTo(secondUser) > 0) {
            String aux = firstUser;
            firstUser = secondUser;
            secondUser = aux;
        }
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public String getFirstUser() {
        return firstUser;
    }

    public String getSecondUser() {
        return secondUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Conversation that = (Conversation) o;
        return firstUser.equals(that.firstUser) && secondUser.equals(that.secondUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstUser, secondUser);
    }
}
