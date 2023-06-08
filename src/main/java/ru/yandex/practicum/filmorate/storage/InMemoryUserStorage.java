package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
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

    public void addUser(User user) {
        user.setId(setIncrementedUserId());
        users.put(user.getId(), user);
    }

    public void updateUser(User user) {

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
    }
}
