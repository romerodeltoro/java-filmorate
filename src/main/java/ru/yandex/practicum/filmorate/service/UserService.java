package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.domain.User;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.NotBlank;

@Service
public class UserService {
    public String checkLoginToHaveNoSpaces(@NotBlank String login) {
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы;");
        }
        return login;
    }

    public User checkNameToBlank(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
