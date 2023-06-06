package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.domain.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    public void addFriend(User user1, User user2) {
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    public void removeFriend(User user1, User user2) {
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
    }

    public List<User> getFriends(User user) {
        return  userStorage.getUsers().stream()
                .filter(u -> u.getFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }
}
