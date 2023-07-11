package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
@Qualifier("inMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> users = new HashMap<>();

    private static int incrementedUserId = 0;

    private long setIncrementedUserId() {
        return ++incrementedUserId;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", id));
        }
    }

    public User addUser(User user) {
        user.setId(setIncrementedUserId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {

        if (users.values().stream().anyMatch(u -> u.getId() == user.getId())) {
            long id = users.values().stream()
                    .filter(u -> u.getId() == user.getId())
                    .findFirst()
                    .get()
                    .getId();
            users.put(id, user);
        } else {
            throw new UserNotFoundException(String.format("Юзера с id %d нет в базе", user.getId()));
        }
        return user;
    }

    @Override
    public void addUserFriend(Long id, Long friendId) {

    }

    @Override
    public void removeUserFriend(Long id, Long friendId) {

    }

    @Override
    public List<User> getUserFriends(Long id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        return null;
    }
}
