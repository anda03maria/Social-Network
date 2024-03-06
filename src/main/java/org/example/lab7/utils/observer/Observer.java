package org.example.lab7.utils.observer;

import org.example.lab7.utils.event.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
