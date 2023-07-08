package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDBStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    private final UserDBStorage userStorage;

    private static Validator validator;


    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    private User user = User.builder()
            .email("practicum@yandex.ru")
            .login("framework")
            .name("Spring Boot")
            .birthday(LocalDate.of(2002, 11, 16))
            .build();

    @Test
    @DisplayName("Получение списка юзеров")
    void findAll() {
        final User createdUser = userStorage.addUser(user);

        final List<User> users = userStorage.getAllUsers();
        int size = users.size();

        assertNotNull(users, "Юзеры не возвращаются.");
        assertEquals(1, size, "Количество юзеров не совпадает.");
    }

    @Test
    @DisplayName("Создание юзера")
    void create() {
        final User createdUser = userStorage.addUser(user);
        final long id = createdUser.getId();

        assertEquals(createdUser,
                userStorage.getUser(id), "Пользователи не совпадают.");
    }

    @Test
    @DisplayName("Создание юзера с некорректными полями")
    void shouldCreateUserWithIncorrectlyFilledField() {
        final User createdUser = User.builder()
                .login("lo gin")
                .birthday(LocalDate.of(2023, 6, 6))
                .build();

        final Set<ConstraintViolation<User>> validates = validator.validate(createdUser);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Создание юзера с граничными полями")
    void shouldCreateUserWithLimitValues() {
        final User createdUser = User.builder()
                .email("email")
                .login("")
                .birthday(LocalDate.of(2023, 5, 26))
                .build();

        final Set<ConstraintViolation<User>> validates = validator.validate(createdUser);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Создание юзера с пустым именем")
    void shouldCreateUserWithEmptyName() {
        final User createdUser = User.builder()
                .email("practicum@yandex.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 1, 10))
                .build();
        userStorage.addUser(createdUser);
        assertEquals(createdUser.getLogin(), createdUser.getName());
    }

    @Test
    @DisplayName("Получение юзера")
    void getUser() {
        final User createdUser = userStorage.addUser(user);
        final long id = createdUser.getId();

        assertEquals(createdUser,
                userStorage.getUser(id), "Пользователи не совпадают.");
    }

    @Test
    @DisplayName("Обновление юзера")
    void update() {
        final long id = userStorage.addUser(user).getId();
        final User updatedUser = User.builder()
                .id(id)
                .email("update-practicum@yandex.ru")
                .login("update-framework")
                .name("Update Spring")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();

        assertEquals(updatedUser, userStorage.updateUser(updatedUser), "Юзеры разные");
    }

    @Test
    @DisplayName("Добавление друзей")
    void addFriends() {
        final User friend = User.builder()
                .email("mail@yandex.ru")
                .login("framework-friend")
                .name("Friend")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();
        long id = userStorage.addUser(user).getId();
        long friendId = userStorage.addUser(friend).getId();

        userStorage.addUserFriend(id, friendId);
        assertNotNull(userStorage.getUserFriends(id));
    }

    @Test
    @DisplayName("Удаление друзей")
    void removeFriends() {
        final User friend = User.builder()
                .email("mail@yandex.ru")
                .login("framework-friend")
                .name("Friend")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();
        long id = userStorage.addUser(user).getId();
        long friendId = userStorage.addUser(friend).getId();

        userStorage.addUserFriend(id, friendId);
        userStorage.removeUserFriend(id, friendId);

        assertTrue(userStorage.getUserFriends(id).isEmpty());
    }

    @Test
    @DisplayName("Получение друзей юзера")
    void getUserFriends() {
        final User friend = User.builder()
                .email("mail@yandex.ru")
                .login("framework-friend")
                .name("Friend")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();
        long id = userStorage.addUser(user).getId();
        long friendId = userStorage.addUser(friend).getId();

        userStorage.addUserFriend(id, friendId);

        assertEquals(1, userStorage.getUserFriends(id).size());

    }

    @Test
    @DisplayName("Получение общих друзей")
    void getCommonFriends() {
        final User user2 = User.builder()
                .email("mail@yandex.ru")
                .login("framework-friend")
                .name("Friend")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();

        final User user3 = User.builder()
                .email("mail@mail.ru")
                .login("usver")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();

        long id = userStorage.addUser(user).getId();
        long user2Id = userStorage.addUser(user2).getId();
        long user3Id = userStorage.addUser(user3).getId();

        userStorage.addUserFriend(id, user2Id);
        userStorage.addUserFriend(id, user3Id);

        assertTrue(userStorage.getCommonFriends(user2Id, user3Id)
                .stream().allMatch(u -> u.getId() == id));

    }
}