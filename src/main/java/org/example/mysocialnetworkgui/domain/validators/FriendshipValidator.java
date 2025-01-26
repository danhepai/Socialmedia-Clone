package org.example.mysocialnetworkgui.domain.validators;

import org.example.mysocialnetworkgui.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity.getFirstUserId() == null || entity.getSecondUserId() == null)
            throw new ValidationException("Friendship is unvalid!");
    }
}
