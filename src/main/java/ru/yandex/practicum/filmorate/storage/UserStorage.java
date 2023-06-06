package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.domain.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();
    void addUser(User user);
    void updateUser(User user);
}
