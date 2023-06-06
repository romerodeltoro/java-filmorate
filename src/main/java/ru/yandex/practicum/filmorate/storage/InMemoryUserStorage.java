package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> users = new HashMap<>();

    private static long incrementedUserId = 0;

    private long setIncrementedUserId() {
        return ++incrementedUserId;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void addUser(User user) {
        user.setId(setIncrementedUserId());
        users.put(user.getId(), user);
    }

    public void updateUser(User user) {
        if (users.values().stream().anyMatch(f -> f.getEmail().equals(user.getEmail()))) {
            long id = users.values().stream()
                    .filter(f -> f.getEmail().equals(user.getEmail()))
                    .findFirst()
                    .get()
                    .getId();
            users.put(id, user);
        } else {
            user.setId(setIncrementedUserId());
            users.put(user.getId(), user);
        }
    }
}
