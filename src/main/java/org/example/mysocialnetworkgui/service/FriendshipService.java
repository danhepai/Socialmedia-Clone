package org.example.mysocialnetworkgui.service;

import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.Message;
import org.example.mysocialnetworkgui.domain.Status;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.exceptions.FriendshipExistsException;
import org.example.mysocialnetworkgui.exceptions.FriendshipNotFoundException;
import org.example.mysocialnetworkgui.exceptions.UserNotFoundException;
import org.example.mysocialnetworkgui.repository.Repository;
import org.example.mysocialnetworkgui.repository.database.PagingRepository;
import org.example.mysocialnetworkgui.utils.events.ChangeEventType;
import org.example.mysocialnetworkgui.utils.events.UserEntityChangeEvent;
import org.example.mysocialnetworkgui.utils.observer.Observable;
import org.example.mysocialnetworkgui.utils.observer.Observer;
import org.example.mysocialnetworkgui.utils.paging.Page;
import org.example.mysocialnetworkgui.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipService implements Observable<UserEntityChangeEvent> {
    private static FriendshipService instance;
    private final Repository<Long, User> repoUser;
    private final Repository<Long, Friendship> repoFriendship;
    private final Repository<Long, Message> repoMessage;

    private List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    public FriendshipService(Repository<Long, User> repoUser, Repository<Long, Friendship> repoFriendship, Repository<Long, Message> repoMessage) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
        this.repoMessage = repoMessage;
    }

    public static FriendshipService getInstance(Repository<Long, User> repoUser, Repository<Long, Friendship> repoFriendship, Repository<Long, Message> repoMessage) {
        if (instance == null) {
            instance = new FriendshipService(repoUser, repoFriendship, repoMessage);
        }
        return instance;
    }

    public Friendship addFriendship(Long userId1, Long userId2) {
        Optional<User> user1 = repoUser.findOne(userId1);
        Optional<User> user2 = repoUser.findOne(userId2);

        if (user1.isEmpty()) {
            throw new UserNotFoundException(userId1);
        }

        if (user2.isEmpty()) {
            throw new UserNotFoundException(userId2);
        }

        Iterable<Friendship> friendships = repoFriendship.findAll();
        for (Friendship f : friendships) {
            if (Objects.equals(f.getFirstUserId(), userId1) && Objects.equals(f.getSecondUserId(), userId2)) {
                if (f.getStatus() == Status.ACCEPTED)
                    throw new FriendshipExistsException("Friendship already exists");
                if (f.getStatus() == Status.PENDING) {
                    f.setStatus(Status.ACCEPTED);
                    f.setDate(LocalDateTime.now());
                }
                notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD, f));
                return f;
            }
        }

        Friendship friendship = new Friendship(userId1, userId2);
        repoFriendship.save(friendship);
        notifyObservers(new UserEntityChangeEvent(ChangeEventType.ADD, friendship));
        return friendship;
    }

    public Friendship deleteFriendship(Long userId1, Long userId2) {
        User user1 = repoUser.findOne(userId1)
                .orElseThrow(() -> new UserNotFoundException(userId1));
        User user2 = repoUser.findOne(userId2)
                .orElseThrow(() -> new UserNotFoundException(userId1));

        Iterable<Friendship> friendships = repoFriendship.findAll();
        for (Friendship friendship : friendships) {
            if ((Objects.equals(friendship.getFirstUserId(), userId1) && Objects.equals(friendship.getSecondUserId(), userId2)) ||
                    (Objects.equals(friendship.getFirstUserId(), userId2) && Objects.equals(friendship.getSecondUserId(), userId1))) {
                repoFriendship.delete(friendship.getId());
                return friendship;
            }
        }

        return null;
    }

    public List<User> getPendingRequests(Long userId) {
        Optional<User> user = repoUser.findOne(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Iterable<Friendship> friendships = repoFriendship.findAll();
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> friendship.getFirstUserId().equals(userId) || friendship.getSecondUserId().equals(userId))
                .filter(friendship -> friendship.getStatus().equals(Status.PENDING))
                .map(friendship -> {
                    Long friendId = friendship.getFirstUserId().equals(userId) ? friendship.getSecondUserId() : friendship.getFirstUserId();
                    return repoUser.findOne(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
                })
                .toList();
    }

    public List<User> getFriends(Long userId) {
        Optional<User> user = repoUser.findOne(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Iterable<Friendship> friendships = repoFriendship.findAll();
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> friendship.getFirstUserId().equals(userId) || friendship.getSecondUserId().equals(userId))
                .filter(friendship -> friendship.getStatus().equals(Status.ACCEPTED))
                .map(friendship -> {
                    Long friendId = friendship.getFirstUserId().equals(userId) ? friendship.getSecondUserId() : friendship.getFirstUserId();
                    return repoUser.findOne(friendId).orElseThrow(() -> new UserNotFoundException(friendId));
                })
                .toList();
    }

    public List<Friendship> getAll() {
        return StreamSupport.stream(repoFriendship.findAll().spliterator(), false)
                .collect(Collectors.toList());
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

    public void acceptFriendship(Long userID, Long secondUserId, int accepted) {
        Iterable<Friendship> allFriendships = repoFriendship.findAll();
        Friendship friendship = StreamSupport.stream(allFriendships.spliterator(), false)
                .filter(f ->
                        (f.getFirstUserId().equals(userID) && f.getSecondUserId().equals(secondUserId)) ||
                                (f.getFirstUserId().equals(secondUserId) && f.getSecondUserId().equals(userID))
                )
                .findFirst()
                .orElse(null);
        if (friendship == null) {
            throw new FriendshipNotFoundException("Friendship not found");
        }
        if (accepted == 1) {
            friendship.setStatus(Status.ACCEPTED);
            friendship.setDate(LocalDateTime.now());
            repoFriendship.update(friendship);
        }
        else if (accepted == 0) {
            friendship.setStatus(Status.REJECTED);
            friendship.setDate(LocalDateTime.now());
            repoFriendship.update(friendship);
        }
    }

    public List<Message> getConversation(Long id1, Long id2) {
        List<Message> messagesWithUser = repoMessage.findConversation(id1, id2);
        return messagesWithUser;
    }

    public Message sendMessage(Long id1, Long id2, String msg, LocalDateTime date){
        Message message = new Message(id1, id2, msg, date);
        repoMessage.save(message);
        return message;
    }
}

