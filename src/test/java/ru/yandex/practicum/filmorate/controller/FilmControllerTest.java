package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private UserController userController;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    private Film film = Film.builder()
            .name("Dune")
            .description("Description")
            .duration(145)
            .releaseDate(LocalDate.of(2021, 9, 21))
            .build();
    private User user = User.builder()
            .email("practicum@yandex.ru")
            .login("framework")
            .name("Spring Boot")
            .birthday(LocalDate.of(2002, 11, 16))
            .build();

    @BeforeEach
    public void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                userController.getUserService().getUserStorage()));
    }

    @Test
    @DisplayName("Получение списка фильмов")
    void findAll() {
        filmController.create(film);
        final List<Film> films = filmController.getFilmService().getFilmStorage().getAllFilms();
        int size = films.size();

        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, size, "Количество фильмов не совпадает.");
    }

    @Test
    @DisplayName("Создание фильма")
    void create() {
        final Film createdFilm = filmController.create(film).getBody();
        final long id = createdFilm.getId();

        assertEquals(createdFilm, filmController.getFilmService().getFilmStorage().getFilm(id), "Фильмы не совпадают.");
    }

    @Test
    @DisplayName("Создание фильма с некорректными полями")
    void shouldCreateFilmWithIncorrectlyFilledField() {
        final Film newfilm = Film.builder()
                .description("Человечество расселилось по далёким планетам, " +
                        "а за власть над обитаемым пространством постоянно борются разные могущественные семьи. " +
                        "В центре противостояния оказывается пустынная планета Арракис. " +
                        "Там обитают гигантские песчаные черви, а в пещерах затаились скитальцы-фремены, " +
                        "но её главная ценность — спайс, самое важное вещество во Вселенной. " +
                        "Тот, кто контролирует Арракис, контролирует спайс, " +
                        "а тот, кто контролирует спайс, контролирует Вселенную.")
                .duration(-1)
                .releaseDate(LocalDate.of(1890, 10, 10))
                .build();

        final Set<ConstraintViolation<Film>> validates = validator.validate(newfilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Создание фильма с граничными полями")
    void shouldCreateFilmWithLimitValues() {
        final Film newfilm = Film.builder()
                .name("")
                .description("Человечество расселилось по далёким планетам, " +
                        "а за власть над обитаемым пространством постоянно борются разные могущественные семьи. " +
                        "В центре противостояния оказывается пустынная планета Арракис. " +
                        "Там обитают гигантские песчаные черви, а в пещерах затаились скитальцы-фремены, " +
                        "но её главная ценность — спайс, самое важное вещество во Вселенной. " +
                        "Тот, кто контролирует Арракис, контролирует спайс, " +
                        "а тот, кто контролирует спайс, контролирует Вселенную.")
                .duration(0)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .build();

        final Set<ConstraintViolation<Film>> validates = validator.validate(newfilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Обновление фильма")
    void update() {
        final long id = filmController.create(film).getBody().getId();

        final Film updateFilm = Film.builder()
                .id(id)
                .name("Update Film")
                .description("New Description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 5, 25))
                .build();

        assertEquals(updateFilm, filmController.update(updateFilm).getBody(), "Фильмы разные");
    }

    @Test
    @DisplayName("Получение фильма")
    void getUser() {
        final Film createdFilm = filmController.create(film).getBody();
        final long id = createdFilm.getId();

        assertEquals(createdFilm,
                filmController.getFilm(id).getBody(), "Пользователи не совпадают.");
    }

    @Test
    @DisplayName("Лайк фильму")
    void addLike() {
        final long id = filmController.create(film).getBody().getId();
        final long userId = userController.create(user).getBody().getId();
        filmController.addLike(id, userId);

        assertTrue(filmController.getFilmService().getFilmStorage().getFilm(id).getLikes().contains(userId));
    }

    @Test
    @DisplayName("Удаление лайка у фильма")
    void removeLike() {
        final long id = filmController.create(film).getBody().getId();
        final long userId = userController.create(user).getBody().getId();
        filmController.addLike(id, userId);
        filmController.removeLike(id, userId);

        assertFalse(filmController.getFilmService().getFilmStorage().getFilm(id).getLikes().contains(userId));
    }

    @Test
    @DisplayName("Получение самых популярных фильмов")
    void getMostLikeFilms() {
        final Film film2 = Film.builder()
                .name("Interstellar")
                .description("Description")
                .duration(169)
                .releaseDate(LocalDate.of(2014, 9, 21))
                .build();
        final Film film3 = Film.builder()
                .name("Alien Covenant")
                .description("Description")
                .duration(110)
                .releaseDate(LocalDate.of(2017, 9, 21))
                .build();
        final User user2 = User.builder()
                .email("mail@yandex.ru")
                .login("framework-friend")
                .name("Friend")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();
        final long idFilm1 = filmController.create(film).getBody().getId();
        final long idFilm2 = filmController.create(film2).getBody().getId();
        final long idFilm3 = filmController.create(film3).getBody().getId();
        final long userId = userController.create(user).getBody().getId();
        final long user2Id = userController.create(user2).getBody().getId();

        filmController.addLike(idFilm1, userId);
        filmController.addLike(idFilm1, user2Id);
        filmController.addLike(idFilm2, userId);

        final List<Film> likedFilms = filmController.getMostLikeFilms(3L).getBody();

        assertEquals(likedFilms, filmController.getFilmService().getMostLikeFilms(3L));
        assertEquals(idFilm1, filmController.getFilmService().getMostLikeFilms(3L).get(0).getId());
        assertEquals(idFilm2, filmController.getFilmService().getMostLikeFilms(3L).get(1).getId());
        assertEquals(idFilm3, filmController.getFilmService().getMostLikeFilms(3L).get(2).getId());
        assertEquals(2, filmController.getFilm(idFilm1).getBody().getLikes().size());
        assertEquals(1, filmController.getFilm(idFilm2).getBody().getLikes().size());
        assertEquals(0, filmController.getFilm(idFilm3).getBody().getLikes().size());

    }
}