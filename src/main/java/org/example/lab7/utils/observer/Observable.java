package org.example.lab7.utils.observer;

import org.example.lab7.utils.event.Event;

public interface Observable<E extends Event>{
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E e);
}
