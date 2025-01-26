package org.example.mysocialnetworkgui.repository.database;

import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.Status;
import org.example.mysocialnetworkgui.domain.validators.Validator;
import org.example.mysocialnetworkgui.exceptions.DatabaseConnectionException;
import org.example.mysocialnetworkgui.utils.paging.Page;
import org.example.mysocialnetworkgui.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBRepository implements PagingRepository<Long, Friendship> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;
    private Connection connection;

    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator) {
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
    public Optional<Friendship> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Friendship friendship = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                LocalDateTime date = resultSet.getTimestamp("friendsFrom").toLocalDateTime();
                Long status = resultSet.getLong("status");

                friendship = new Friendship(user1_id, user2_id, date);
                friendship.setStatus(Status.fromOrdinal(status));
                friendship.setId(id);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(friendship);
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user1_id = resultSet.getLong("user1_id");
                Long user2_id = resultSet.getLong("user2_id");
                LocalDateTime date = resultSet.getTimestamp("friendsFrom").toLocalDateTime();
                Long status = resultSet.getLong("status");

                Friendship friendship = new Friendship(user1_id, user2_id, date);
                friendship.setStatus(Status.fromOrdinal(status));
                friendship.setId(id);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (user1_id, user2_id, friendsfrom) VALUES (?, ?, ?)")) {
            statement.setLong(1, entity.getFirstUserId());
            statement.setLong(2, entity.getSecondUserId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Optional<Friendship> friendship = findOne(id);
        if (friendship.isEmpty())
            return Optional.empty();
        int rez = -1;
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id = ?")
        ) {
            statement.setLong(1, id);
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return friendship;
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET user1_id = ?, user2_id = ?, friendsfrom = ?, status = ? WHERE id = ?")
        ) {
            statement.setLong(1, entity.getFirstUserId());
            statement.setLong(2, entity.getSecondUserId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setLong(4, entity.getStatus().ordinal());
            statement.setLong(5, entity.getId());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Long giveNewId() {
        return null;
    }

    @Override
    public List<Message> findConversation(Long id1, Long id2) {
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

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        return null;
    }
}
