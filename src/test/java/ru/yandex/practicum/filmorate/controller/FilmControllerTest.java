package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.domain.Film;

import javax.validation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
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

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("Получение списка фильмов")
    void findAll() {
        filmController.create(film);
        final List<Film> films = new ArrayList<>(filmController.getFilms().values());
        int size = films.size();

        assertNotNull(films, "Фильмы не возвращаются.");
        assertEquals(1, size, "Количество фильмов не совпадает.");
    }

    @Test
    @DisplayName("Создание фильма")
    void create() {
        final Film createdFilm = filmController.create(film).getBody();
        final long id = createdFilm.getId();

        assertEquals(createdFilm, filmController.getFilms().get(id), "Фильмы не совпадают.");
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
        final Film updateFilm = Film.builder()
                .id(1L)
                .name("Update Film")
                .description("New Description")
                .duration(200)
                .releaseDate(LocalDate.of(2000, 5, 25))
                .build();

        filmController.create(film).getBody();
        assertEquals(updateFilm, filmController.update(updateFilm).getBody(), "Фильмы разные");
    }
}