package org.example.mysocialnetworkgui.repository.memory;


import org.example.mysocialnetworkgui.domain.Entity;
import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.validators.ValidationException;
import org.example.mysocialnetworkgui.domain.validators.Validator;
import org.example.mysocialnetworkgui.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private final Validator<E> validator;
    protected final Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return this.entities.values();
    }

    @Override
    public Optional<E> save(E entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null!");
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);
        E result = entities.put(entity.getId(), entity);
        return result == null ? Optional.of(entity) : Optional.empty();
    }

    @Override
    public ID giveNewId() {
        return null;
    }

    @Override
    public List<Message> findConversation(ID id1, ID id2) {
        return List.of();
    }

    @Override
    public String savePicture(String pictureUrl, Long entity) {
        return "";
    }

    @Override
    public String hashPassword(String password) {
        return "";
    }
}
