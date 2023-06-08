package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
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

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    @DisplayName("Получение списка юзеров")
    void findAll() {
        userController.create(user);
        final List<User> users = userController.getUserService().getUserStorage().getAllUsers();
        int size = users.size();

        assertNotNull(users, "Юзеры не возвращаются.");
        assertEquals(1, size, "Количество юзеров не совпадает.");
    }

    @Test
    @DisplayName("Создание юзера")
    void create() {
        final User createdUser = userController.create(user).getBody();
        final long id = createdUser.getId();

        assertEquals(createdUser,
                userController.getUserService().getUserStorage().getUser(id), "Пользователи не совпадают.");
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
        userController.create(createdUser);
        assertEquals(createdUser.getLogin(), createdUser.getName());
    }

    @Test
    @DisplayName("Получение юзера")
    void getUser() {
        final User createdUser = userController.create(user).getBody();
        final long id = createdUser.getId();

        assertEquals(createdUser,
                userController.getUser(id).getBody(), "Пользователи не совпадают.");
    }

    @Test
    @DisplayName("Обновление юзера")
    void update() {
        final long id = userController.create(user).getBody().getId();

        final User updatedUser = User.builder()
                .id(id)
                .email("update-practicum@yandex.ru")
                .login("update-framework")
                .name("Update Spring")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();

        assertEquals(updatedUser, userController.update(updatedUser).getBody(), "Юзеры разные");
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
        long id = userController.create(user).getBody().getId();
        long friendId = userController.create(friend).getBody().getId();

        userController.addFriends(id, friendId);
        assertNotNull(userController.getUserService().getUserStorage().getUser(id).getFriends());
        assertNotNull(userController.getUserService().getUserStorage().getUser(friendId).getFriends());

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
        long id = userController.create(user).getBody().getId();
        long friendId = userController.create(friend).getBody().getId();

        userController.addFriends(id, friendId);
        userController.removeFriends(id, friendId);

        assertTrue(userController.getUserService().getUserStorage().getUser(id).getFriends().isEmpty());
        assertTrue(userController.getUserService().getUserStorage().getUser(friendId).getFriends().isEmpty());
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
        long id = userController.create(user).getBody().getId();
        long friendId = userController.create(friend).getBody().getId();

        userController.addFriends(id, friendId);

        assertEquals(1, userController.getUserFriends(id).getBody().size());

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

        long id = userController.create(user).getBody().getId();
        long user2Id = userController.create(user2).getBody().getId();
        long user3Id = userController.create(user3).getBody().getId();

        userController.addFriends(id, user2Id);
        userController.addFriends(id, user3Id);

        assertTrue(userController.getCommonFriends(user2Id, user3Id).getBody()
                .stream().allMatch(u -> u.getId() == id));

    }
}