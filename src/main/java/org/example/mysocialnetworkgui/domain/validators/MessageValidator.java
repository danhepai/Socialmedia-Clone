package org.example.mysocialnetworkgui.domain.validators;

import org.example.mysocialnetworkgui.domain.Message;

import java.util.Objects;

public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {
        if (entity.getFrom() == null || entity.getTo() == null || entity.getMsg().equals("")) {
            throw new ValidationException("From and To are required");
        }
    }
}
