package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class UserService {


    @Autowired
    @Qualifier("userStorage")
    private UserStorage userStorage;


    /**
     * Проверка поля Имя, если оно пустое, то заполняется как Логин
     */
    public User checkNameToBlank(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }


    /**
     * Создание пользователя
     */
    public User createUser(User user) {
        if (userStorage.getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException(String.format(

                    "Пользователь с электронной почтой %s уже зарегистрирован.", user.getEmail()
            ));
        }
        userStorage.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new UserNotFoundException(String.format(
                    "Пользователя с id %d нет в базе", user.getId())
            );
        }
        userStorage.updateUser(user);
        return user;
    }

    /**
     * Добавляем пользователю в друзья другого пользователя
     */
    public void addFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", id));
        } else if (userStorage.getUser(friendId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", friendId));
        } else {
            userStorage.addUserFriend(id, friendId);
        }
    }

    /**
     * Удаляем пользователя из друзей
     */
    public void removeFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", id));
        } else if (userStorage.getUser(friendId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", friendId));
        } else {
            userStorage.removeUserFriend(id, friendId);
        }
    }

    /**
     * Получаем список друзей пользователя
     */
    public List<User> getFriends(Long id) {
        return userStorage.getUserFriends(id);
    }

    /**
     * Получаем список друзей, общих с другим пользователем
     */
    public List<User> getCommonFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);

    }
}
