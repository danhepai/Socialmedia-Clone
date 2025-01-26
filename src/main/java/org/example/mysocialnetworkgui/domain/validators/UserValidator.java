package org.example.mysocialnetworkgui.domain.validators;

import org.example.mysocialnetworkgui.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if (entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            throw new ValidationException("User is unvalid!");
    }
}
