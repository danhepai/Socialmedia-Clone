package org.example.mysocialnetworkgui.repository.database;

import org.example.mysocialnetworkgui.domain.Entity;
import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.validators.Validator;
import org.example.mysocialnetworkgui.exceptions.DatabaseConnectionException;
import org.example.mysocialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class AbstractDataBaseRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    // DriverManager
    private Connection connection;
    private Validator<E> validator;

    protected void AbstractDatabaseRepository(Validator<E> validator) {
        this.validator = validator;
        try {
            this.connection = DriverManager.getConnection("jdbc:postgres://localhost:5432/socialnetwork", "postgres", "qwankido1");
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Connection to database error!");
        }
    }

    @Override
    public Optional<E> findOne(ID id) {
        return Optional.empty();
    }

    @Override
    public Iterable<E> findAll() {
        return null;
    }

    @Override
    public Optional<E> save(E entity) {
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) {
        return Optional.empty();
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
