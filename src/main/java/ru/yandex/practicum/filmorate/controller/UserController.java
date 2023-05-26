package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Getter
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private HashMap<Long, User> users = new HashMap<>();
    private final UserService userService;
    private static long incrementedUserId = 0;

    private long setIncrementedUserId() {
        return ++incrementedUserId;
    }

    @GetMapping
    public ResponseEntity<Collection<User>> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return ResponseEntity.ok().body(users.values());
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        ResponseEntity.ok(userService.checkLoginToHaveNoSpaces(user.getLogin()));
        ResponseEntity.ok(userService.checkNameToBlank(user));
        user.setId(setIncrementedUserId());
        users.put(user.getId(), user);
        log.info("Добавлен новый юзер: '{}'", user);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        if (users.values().stream().anyMatch(f -> f.getName().equals(user.getName()))) {
            User updateUser = (User) users.values().stream().filter(f -> f.getName().equals(user.getName()));
            users.put(updateUser.getId(), user);
            log.info("User '{}' - обновлен", user);
        } else {
            users.put(user.getId(), user);
            log.info("User '{}' - обновлен", user);
        }
        return ResponseEntity.ok().body(user);
    }

}
