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
                userController.getUserService().getUserStorage().getUser(id), "Фильмы не совпадают.");
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
    @DisplayName("Обновление юзера")
    void update() {
        final User updatedUser = User.builder()
                .id(3L)
                .email("update-practicum@yandex.ru")
                .login("update-framework")
                .name("Update Spring")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();

        userController.create(user).getBody();
        assertEquals(updatedUser, userController.update(updatedUser).getBody(), "Юзеры разные");
    }
}