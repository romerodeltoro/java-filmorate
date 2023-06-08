package ru.yandex.practicum.filmorate.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;


    /* Проверка поля Имя, если оно пустое, то заполняется как Логин */

    public User checkNameToBlank(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }


    /* Создание пользователя */
    public User createUser(User user) {
        if (userStorage.getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с электронной почтой %s уже зарегистрирован.",
                    user.getEmail()
            ));
        }
        userStorage.addUser(user);

        return user;
    }

    /* Добавляем пользователю в друзья другого пользователя */
    public void addFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", id));
        } else if (userStorage.getUser(friendId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", friendId));
        } else {
            userStorage.getUser(id).getFriends().add(friendId);
            userStorage.getUser(friendId).getFriends().add(id);
        }
    }

    /* Удаляем пользователя из друзей */
    public void removeFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", id));
        } else if (userStorage.getUser(friendId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", friendId));
        } else {
            userStorage.getUser(id).getFriends().remove(friendId);
            userStorage.getUser(friendId).getFriends().remove(id);
        }
    }

    /* Получаем список друзей пользователя */
    public List<User> getFriends(Long id) {
        List<Long> friendsId = new ArrayList<>(userStorage.getUser(id).getFriends());
        return userStorage.getAllUsers().stream()
                .filter(u -> friendsId.contains(u.getId()))
                .collect(Collectors.toList());

    }

    /* Получаем список друзей, общих с другим пользователем */
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<Long> myId = new ArrayList<>(userStorage.getUser(id).getFriends());
        List<Long> friendId = new ArrayList<>(userStorage.getUser(otherId).getFriends());
        return userStorage.getAllUsers().stream()
                .filter(u -> myId.contains(u.getId()))
                .filter(u -> friendId.contains(u.getId()))
                .collect(Collectors.toList());
    }

}
