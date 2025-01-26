package org.example.mysocialnetworkgui.repository.database;

import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.validators.Validator;
import org.example.mysocialnetworkgui.exceptions.DatabaseConnectionException;
import org.example.mysocialnetworkgui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    private Connection connection;

    public MessageDBRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Connection to database error!");
        }
    }


    @Override
    public Optional<Message> findOne(Long id) {
        return null;
    }

    @Override
    public List<Message> findConversation(Long id1, Long id2) {
        if (id2 == null || id1 == null)
            throw new IllegalArgumentException("Id cannot be null!");

        List<Message> messages = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE (fromid = ? AND toid = ?) OR (fromid = ? AND toid = ?)")) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long userFrom = resultSet.getLong("fromid");
                Long userTo = resultSet.getLong("toid");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Message conversationMsg = new Message(userFrom, userTo, message, date);
                messages.add(conversationMsg);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public String savePicture(String pictureUrl, Long entity) {
        return "";
    }

    @Override
    public String hashPassword(String password) {
        return "";
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO message(fromid, toid, message, date) VALUES (?, ?, ?, ?)")) {
            statement.setLong(1, entity.getFrom());
            statement.setLong(2, entity.getTo());
            statement.setString(3, entity.getMsg());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public Long giveNewId() {
        return 0L;
    }
}
