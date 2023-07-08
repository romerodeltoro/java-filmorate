package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    void addUserFriend(Long id, Long friendId);

    void removeUserFriend(Long id, Long friendId);

    List<User> getUserFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);

}
