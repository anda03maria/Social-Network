package org.example.lab7.domain;

import java.util.Objects;

/**
 * Generic Pair
 * @param <E1>, first element from generic pair
 * @param <E2>, second element from generic pair
 */
public abstract class Pair <E1, E2> {

    private E1 first;
    private E2 second;

    public E1 getFirst() {
        return first;
    }

    public void setFirst(E1 first) {
        this.first = first;
    }

    public E2 getSecond() {
        return second;
    }

    public void setSecond(E2 second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}