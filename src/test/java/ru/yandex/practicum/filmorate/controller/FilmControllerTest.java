package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDBStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {
  
    private final UserDBStorage userStorage;
    private final FilmDBStorage filmStorage;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    private Film film = new Film(
            "Dune",
            "Description",
            LocalDate.of(2021, 9, 21),
            145,
            Mpa.builder().id(3).name("PG-13").build());

    private User user = User.builder()
            .email("practicum@yandex.ru")
            .login("framework")
            .name("Spring Boot")
            .birthday(LocalDate.of(2002, 11, 16))
            .build();
    private User user = User.builder()
            .email("practicum@yandex.ru")
            .login("framework")
            .name("Spring Boot")
            .birthday(LocalDate.of(2002, 11, 16))
            .build();


    @Test
    @DisplayName("Получение списка фильмов")
    void findAll() {
        filmStorage.addFilm(film);
        final Collection<Film> films = filmStorage.getAllFilms();
        int size = films.size();

        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, size, "Количество фильмов не совпадает.");
    }

    @Test
    @DisplayName("Создание фильма")
    void create() {
        final Film createdFilm = filmStorage.addFilm(film);
        final long id = createdFilm.getId();

        System.out.println();

        assertEquals(createdFilm,
                filmStorage.getFilm(id), "Фильмы не совпадают.");
    }

    @Test
    @DisplayName("Получение фильма")
    void getFilm() {
        final Film createdFilm = filmStorage.addFilm(film);
        final long id = createdFilm.getId();

        assertEquals(createdFilm,
                filmStorage.getFilm(id), "Пользователи не совпадают.");

    }

    @Test
    @DisplayName("Создание фильма с некорректными полями")
    void shouldCreateFilmWithIncorrectlyFilledField() {
        final Film newfilm = new Film();
        newfilm.setDescription("Человечество расселилось по далёким планетам, " +
                "а за власть над обитаемым пространством постоянно борются разные могущественные семьи. " +
                "В центре противостояния оказывается пустынная планета Арракис. " +
                "Там обитают гигантские песчаные черви, а в пещерах затаились скитальцы-фремены, " +
                "но её главная ценность — спайс, самое важное вещество во Вселенной. " +
                "Тот, кто контролирует Арракис, контролирует спайс, " +
                "а тот, кто контролирует спайс, контролирует Вселенную.");
        newfilm.setDuration(-1);
        newfilm.setReleaseDate(LocalDate.of(1890, 10, 10));

        final Set<ConstraintViolation<Film>> validates = validator.validate(newfilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Создание фильма с граничными полями")
    void shouldCreateFilmWithLimitValues() {
        final Film newfilm = new Film();
        newfilm.setName("");
        newfilm.setDescription(
                "Человечество расселилось по далёким планетам, " +
                        "а за власть над обитаемым пространством постоянно борются разные могущественные семьи. " +
                        "В центре противостояния оказывается пустынная планета Арракис. " +
                        "Там обитают гигантские песчаные черви, а в пещерах затаились скитальцы-фремены, " +
                        "но её главная ценность — спайс, самое важное вещество во Вселенной. " +
                        "Тот, кто контролирует Арракис, контролирует спайс, " +
                        "а тот, кто контролирует спайс, контролирует Вселенную.");
        newfilm.setDuration(0);
        newfilm.setReleaseDate(LocalDate.of(1895, 12, 28));

        final Set<ConstraintViolation<Film>> validates = validator.validate(newfilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage())
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("Обновление фильма")
    void update() {

        final long id = filmStorage.addFilm(film).getId();

        final Film updateFilm = new Film();
        updateFilm.setName("Update Film");
        updateFilm.setDescription("New Description");
        updateFilm.setDuration(200);
        updateFilm.setReleaseDate(LocalDate.of(2000, 5, 25));
        updateFilm.setMpa(Mpa.builder().id(2).build());

        assertEquals(updateFilm, filmStorage.updateFilm(updateFilm), "Фильмы разные");
    }

    @Test
    @DisplayName("Лайк фильму")
    void addLike() {
        final long filmId = filmStorage.addFilm(film).getId();
        final long userId = userStorage.addUser(user).getId();

        filmStorage.addLikeToFilm(filmId, userId);

        assertEquals(1, filmStorage.getFilm(filmId).getLikes(), "Разное колличество лайков");
    }

    @Test
    @DisplayName("Удаление лайка у фильма")
    void removeLike() {
        final long filmId = filmStorage.addFilm(film).getId();
        final long userId = userStorage.addUser(user).getId();

        filmStorage.addLikeToFilm(filmId, userId);
        filmStorage.removeLike(filmId, userId);

        assertEquals(0, filmStorage.getFilm(filmId).getLikes(), "Разное колличество лайков");
    }

    @Test
    @DisplayName("Получение самых популярных фильмов")
    void getMostLikeFilms() {
        final Film film2 = new Film();
        film2.setName("Interstellar");
        film2.setDescription("Description");
        film2.setDuration(169);
        film2.setReleaseDate(LocalDate.of(2014, 9, 21));
        film2.setMpa(Mpa.builder().id(1).build());

        final Film film3 = new Film();
        film3.setName("Alien Covenant");
        film3.setDescription("Description");
        film3.setDuration(110);
        film3.setReleaseDate(LocalDate.of(2017, 9, 21));
        film3.setMpa(Mpa.builder().id(2).build());

        final User user2 = User.builder()
                .email("mail@yandex.ru")
                .login("framework-friend")
                .name("Friend")
                .birthday(LocalDate.of(2002, 11, 16))
                .build();
        final long idFilm1 = filmStorage.addFilm(film).getId();
        final long idFilm2 = filmStorage.addFilm(film2).getId();
        final long idFilm3 = filmStorage.addFilm(film3).getId();
        final long userId = userStorage.addUser(user).getId();
        final long user2Id = userStorage.addUser(user2).getId();

        filmStorage.addLikeToFilm(idFilm1, userId);
        filmStorage.addLikeToFilm(idFilm1, user2Id);
        filmStorage.addLikeToFilm(idFilm2, userId);

        assertEquals(idFilm1, filmStorage.getMostLikeFilms().stream().findFirst().get().getId());
        assertEquals(idFilm2, filmStorage.getMostLikeFilms().stream().skip(1).findFirst().get().getId());

        assertEquals(2, filmStorage.getFilm(idFilm1).getLikes());
        assertEquals(1, filmStorage.getFilm(idFilm2).getLikes());
        assertEquals(0, filmStorage.getFilm(idFilm3).getLikes());
    }

    @Test
    @DisplayName("Обнавление фильма, добавление жанров ")
    void addGenreToFilm() {
        long id = filmStorage.addFilm(film).getId();

        final Film filmWithGenre = new Film();
        filmWithGenre.setId(id);
        filmWithGenre.setName("Interstellar");
        filmWithGenre.setDescription("Description");
        filmWithGenre.setDuration(169);
        filmWithGenre.setReleaseDate(LocalDate.of(2014, 9, 21));
        filmWithGenre.setMpa(Mpa.builder().id(1).build());
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(1).build());
        filmWithGenre.setGenres(genres);

        filmStorage.updateFilm(filmWithGenre);

        assertEquals(1, filmStorage.getFilm(id).getGenres().size());
    }

    @Test
    @DisplayName("Обнавление фильма, удаление жанров ")
    void deleteGenreFromFilm() {
        long id = filmStorage.addFilm(film).getId();

        final Film filmWithGenre = new Film();
        filmWithGenre.setId(id);
        filmWithGenre.setName("Interstellar");
        filmWithGenre.setDescription("Description");
        filmWithGenre.setDuration(169);
        filmWithGenre.setReleaseDate(LocalDate.of(2014, 9, 21));
        filmWithGenre.setMpa(Mpa.builder().id(1).build());
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(1).build());
        filmWithGenre.setGenres(genres);

        filmStorage.updateFilm(filmWithGenre);
        filmStorage.updateFilm(film);

        assertEquals(0, filmStorage.getFilm(id).getGenres().size());
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