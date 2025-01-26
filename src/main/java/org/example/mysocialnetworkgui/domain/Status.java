package org.example.mysocialnetworkgui.domain;

public enum Status {
    PENDING, ACCEPTED, REJECTED;

    public static Status fromOrdinal(Long ordinal) {
        return values()[ordinal.intValue()];
    }
}
