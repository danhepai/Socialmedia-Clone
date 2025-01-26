package org.example.mysocialnetworkgui.repository.database;

import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.domain.validators.Validator;
import org.example.mysocialnetworkgui.exceptions.DatabaseConnectionException;
import org.example.mysocialnetworkgui.utils.paging.Page;
import org.example.mysocialnetworkgui.utils.paging.Pageable;
import org.example.mysocialnetworkgui.repository.PasswordHasher;

import java.util.*;

import java.sql.*;

public class UserDBRepository implements PagingRepository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;
    private Connection connection;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("AICI Connection to database error!");
        }
    }


    @Override
    public Optional<User> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                String picture = resultSet.getString("profilepicture");

                user = new User(firstName, lastName, password, picture);
                user.setId(id);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                String picture = resultSet.getString("profilepicture");

                User user = new User(firstName, lastName, password, picture);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (first_name, last_name, password, profilepicture) VALUES (?, ?, ?, ?)")
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getPassword());
            statement.setString(4, entity.getPicture());
            System.out.println(hashPassword(entity.getPassword()));

            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public String savePicture(String pictureUrl, Long userId) {
        if (pictureUrl == null)
            throw new IllegalArgumentException("Picture url cannot be null!");
        //validator.validatePic(pictureUrl);
        int rez = -1;
        try (
                PreparedStatement statement = connection.prepareStatement("UPDATE users SET profilepicture = ? WHERE id = ?")
        ) {
            statement.setLong(2, userId);
            statement.setString(1, pictureUrl);

            rez = statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
        if (rez > 0){
            return pictureUrl;
        }
        return null;
    }



    @Override
    public String hashPassword(String password) {
        return PasswordHasher.hashPassword(password);
    }

    @Override
    public Optional<User> delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Optional<User> user = findOne(id);
        if (user.isEmpty())
            return Optional.empty();
        int rez = -1;
        try (//Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")
        ) {
            statement.setLong(1, id);
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return user;
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ? WHERE id = ?")
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());
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
    public Page<User> findAllOnPage(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        List<User> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from users ORDER BY id LIMIT ? OFFSET ?")) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, password);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int totalNumberOfUsers = 0;
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM users")) {
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    totalNumberOfUsers = result.getInt("count");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(users, totalNumberOfUsers);
    }
}
