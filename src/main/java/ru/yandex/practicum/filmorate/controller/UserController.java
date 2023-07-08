package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@Getter
@Validated
@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    /**
     * Выводим всех пользователей
     */
    @GetMapping
    public ResponseEntity<Collection<User>> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return ResponseEntity.ok().body(userService.getUserStorage().getAllUsers());
    }

    /**
     * Создание пользователя
     */
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        ResponseEntity.ok(userService.checkNameToBlank(user));
        userService.createUser(user);
        log.info("Добавлен новый пользователь: '{}'", user);
        return ResponseEntity.ok().body(user);
    }

    /**
     * Обновляем пользователя
     */
    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.info("Пользователь '{}' - обновлен", user);
        return ResponseEntity.ok().body(user);
    }

    /**
     * Получаем пользователя
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Получены данные о пользователе с id '{}'", id);
        return ResponseEntity.ok().body(userService.getUserStorage().getUser(id));
    }

    /**
     * Добовляем в список друзей пользователя
     */
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriends(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь с id '{}' добавлен в друзья юзеру в с id '{}'", friendId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаляем из друзей пользователя
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriends(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        log.info("Пользователь с id '{}' удален из друзей юзера с id '{}'", friendId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получаем список друзей пользователя
     */
    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getUserFriends(@PathVariable Long id) {
        log.info("Получен список друзей пользователя с id '{}'", id);
        return ResponseEntity.ok().body(userService.getFriends(id));
    }

    /**
     * Выводим общих друзей пользователей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        log.info("Получен список общих друзей пользователя с id '{}' и пользователя с id '{}'", id, otherId);
        return ResponseEntity.ok().body(userService.getCommonFriends(id, otherId));
    }
}
