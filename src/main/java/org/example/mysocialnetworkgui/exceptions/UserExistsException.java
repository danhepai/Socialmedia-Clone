package org.example.mysocialnetworkgui.exceptions;

import org.example.mysocialnetworkgui.domain.User;

public class UserExistsException extends RuntimeException {
    public UserExistsException(User u) {
        super("User" + u.getFirstName() + " already exists");
    }
}
