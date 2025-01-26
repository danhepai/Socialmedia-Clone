package org.example.mysocialnetworkgui.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}