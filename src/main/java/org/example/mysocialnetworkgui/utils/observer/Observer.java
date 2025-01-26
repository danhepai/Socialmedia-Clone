package org.example.mysocialnetworkgui.utils.observer;


import org.example.mysocialnetworkgui.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}