package org.example.mysocialnetworkgui.service;

import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.exceptions.ServiceException;
import org.example.mysocialnetworkgui.repository.Repository;

import java.util.*;

public class Service<ID> {
    private static Service<Long> instance = null;
    private final Repository<Long, User> repoUser;
    private final Repository<Long, Friendship> repoFriendship;

    private Service(Repository<Long, User> repoUser, Repository<Long, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
    }

    public static Service<Long> getInstance(Repository<Long, User> repoUtilizator, Repository<Long, Friendship> repoPrietenie) {
        if (instance == null) {
            instance = new Service<Long>(repoUtilizator, repoPrietenie);
        }
        return instance;
    }

    public void addUser(String firstName, String lastName) {
//        User utilizator = new User(firstName, lastName);
//        utilizator.setId(repoUser.giveNewId());
//        repoUser.save(utilizator);
    }

    public Optional<User> removeUser(Long id) {
        repoFriendship.findAll().forEach(prietenie -> {
            if (prietenie.getFirstUserId().equals(id) || prietenie.getSecondUserId().equals(id)) {
                repoFriendship.delete(prietenie.getId());
            }
        });
        return repoUser.delete(id);
    }

    public Optional<User> getUser(Long id) {
        return repoUser.findOne(id);
    }

    public Iterable<User> getAllUsers() {
        return repoUser.findAll();
    }

    public void addFriendship(Long user1Id, Long user2Id) {
        if (repoUser.findOne(user1Id).isEmpty() || repoUser.findOne(user2Id).isEmpty()) {
            throw new ServiceException("Id doesn't exist!");
        }
        if (user1Id.equals(user2Id)) {
            throw new ServiceException("Cannot add prietenie between same utilizator!");
        }

        Friendship prietenie = new Friendship(user1Id, user2Id);
        prietenie.setId(repoFriendship.giveNewId());
        repoFriendship.save(prietenie);
    }

    public Optional<Friendship> removeFriendship(Long id) {
        return repoFriendship.delete(id);
    }

    public Iterable<Friendship> getAllFriendships() {
        return repoFriendship.findAll();
    }

    public int numberOfCommunities() {
        Iterable<User> utilizatori = repoUser.findAll();
        Set<Long> vizUtilizatori = new HashSet<>();
        int count = 0;

        for (User u : utilizatori)
            if (!vizUtilizatori.contains(u.getId())) {
                ++count;
                dfs(u.getId(), vizUtilizatori);
            }

        return count;
    }

    private List<Long> dfs(Long utilizatorId, Set<Long> vizUtilizatori) {
        List<Long> component = new ArrayList<>();
        vizUtilizatori.add(utilizatorId);
        component.add(utilizatorId);

        repoFriendship.findAll().forEach(p -> {
            if (p.getFirstUserId().equals(utilizatorId) && !vizUtilizatori.contains(p.getSecondUserId())) {
                component.addAll(dfs(p.getSecondUserId(), vizUtilizatori));
            } else if (p.getSecondUserId().equals(utilizatorId) && !vizUtilizatori.contains(p.getFirstUserId())) {
                component.addAll(dfs(p.getFirstUserId(), vizUtilizatori));
            }
        });

        return component;
    }

    public Iterable<User> mostSociableCommunity() {
        Iterable<Long> sociableCommunity = new ArrayList<>();
        Iterable<User> utilizatori = repoUser.findAll();
        Set<Long> vizUtilizatori = new HashSet<>();

        int max = -1;
        for (User u : utilizatori) {
            if (!vizUtilizatori.contains(u.getId())) {
                List<Long> aux = dfs(u.getId(), vizUtilizatori);
                int l = longestPath(aux);
                if (l > max) {
                    sociableCommunity = aux;
                    max = l;
                }
            }
        }

        List<User> sociableCommunityUtilizatori = new ArrayList<>();
        for (Long u : sociableCommunity) {
            Optional<User> utilizator = repoUser.findOne(u);
            utilizator.ifPresent(sociableCommunityUtilizatori::add);
        }

        return sociableCommunityUtilizatori;
    }

    private int longestPath(List<Long> community) {
        int maxLength = 0;
        for (Long u : community) {
            Set<Long> visited = new HashSet<>();
            maxLength = Math.max(maxLength, dfsLongestPath(u, visited));
        }
        return maxLength;
    }

    private int dfsLongestPath(Long utilizatorId, Set<Long> visited) {
        visited.add(utilizatorId);
        int maxLength = 0;

        for (Friendship p : repoFriendship.findAll()) {
            Long neighborId = null;
            if (p.getFirstUserId().equals(utilizatorId) && !visited.contains(p.getSecondUserId())) {
                neighborId = p.getSecondUserId();
            } else if (p.getSecondUserId().equals(utilizatorId) && !visited.contains(p.getFirstUserId())) {
                neighborId = p.getFirstUserId();
            }

            if (neighborId != null) {
                maxLength = Math.max(maxLength, 1 + dfsLongestPath(neighborId, visited));
            }
        }

        visited.remove(utilizatorId);
        return maxLength;
    }

}
