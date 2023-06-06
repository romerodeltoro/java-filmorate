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

@Slf4j
@Getter
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<Collection<User>> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return ResponseEntity.ok().body(userService.getUserStorage().getUsers());
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        ResponseEntity.ok(userService.checkLoginToHaveNoSpaces(user.getLogin()));
        ResponseEntity.ok(userService.checkNameToBlank(user));
        userService.getUserStorage().addUser(user);
        log.info("Добавлен новый юзер: '{}'", user);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        userService.getUserStorage().updateUser(user);
        log.info("User '{}' - обновлен", user);
        return ResponseEntity.ok().body(user);
    }

    /*@PutMapping ("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(
            @Valid
            @PathVariable int id,
            @PathVariable int friendId) {

    }
*/
}
