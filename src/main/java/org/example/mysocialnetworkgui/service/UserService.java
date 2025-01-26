package org.example.mysocialnetworkgui.service;

import javafx.scene.control.TextField;
import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.Status;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.exceptions.FriendshipExistsException;
import org.example.mysocialnetworkgui.exceptions.FriendshipNotFoundException;
import org.example.mysocialnetworkgui.exceptions.UserExistsException;
import org.example.mysocialnetworkgui.exceptions.UserNotFoundException;
import org.example.mysocialnetworkgui.repository.PasswordHasher;
import org.example.mysocialnetworkgui.repository.Repository;
import org.example.mysocialnetworkgui.repository.database.PagingRepository;
import org.example.mysocialnetworkgui.utils.events.ChangeEventType;
import org.example.mysocialnetworkgui.utils.events.UserEntityChangeEvent;
import org.example.mysocialnetworkgui.utils.observer.Observable;
import org.example.mysocialnetworkgui.utils.observer.Observer;
import org.example.mysocialnetworkgui.utils.paging.Page;
import org.example.mysocialnetworkgui.utils.paging.Pageable;

import java.io.Console;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService implements Observable<UserEntityChangeEvent> {
    private static UserService instance;
    private final PagingRepository<Long, User> repoUser;
    private final Repository<Long, Friendship> repoFriendship;

    private List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    public UserService(PagingRepository<Long, User> repoUser, Repository<Long, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
    }

    public static UserService getInstance(PagingRepository<Long, User> repoUser, Repository<Long, Friendship> repoFriendship) {
        if (instance == null) {
            instance = new UserService(repoUser, repoFriendship);
        }
        return instance;
    }

    public User addUser(String firstName, String lastName, String password) {
        User user = new User(firstName, lastName, PasswordHasher.hashPassword(password));
        System.out.println(user.getPassword());
        repoUser.save(user).ifPresent(u ->{
            throw new UserExistsException(u);
        });
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD, null));
        return user;
    }

    public User deleteUser(long userId) {
        User user = repoUser.delete(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Friendship> toBeDeleted = StreamSupport.stream(repoFriendship.findAll().spliterator(), false)
                .filter(f -> f.getFirstUserId().equals(userId) || f.getSecondUserId().equals(userId)).toList();
        toBeDeleted.forEach(f -> repoFriendship.delete(f.getId()));
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE, null));
        return user;
    }

    public User getUserById(Long id) {
        return repoUser.findOne(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public Iterable<User> getAllUsers() {
        return repoUser.findAll();
    }

    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent t) {
        observers.forEach(observer -> observer.update(t));
    }

    public User getUserByFirstNameAndSecondName(String firstName, String lastName) {
        Iterable<User> users = repoUser.findAll();
        for (User u : users) {
            if (u.getFirstName().equals(firstName) && u.getLastName().equals(lastName))
                return u;
        }
        return null;
    }

    public Page<User> findAllUsersOnPage(Pageable pageable) {
        return repoUser.findAllOnPage(pageable);
    }

    public String getPictureUrl(Long userId) {
        return getUserById(userId).getPicture();
    }

    public void updateProfilePictureInDatabase(Long userId, String pictureUrl) {
        repoUser.savePicture(pictureUrl, userId);
    }
}
