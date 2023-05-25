package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Getter
@Validated
@RestController
@RequestMapping("films")
public class FilmController {
    private HashMap<Long, Film> films = new HashMap<>();
    private static long incrementedFilmId = 0;

    private long setIncrementedFilmId() {

        return ++incrementedFilmId;
    }

    @GetMapping
    public Collection<Film> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(setIncrementedFilmId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: id - '{}', name - '{}'", film.getId(), film.getName());
        return film;

    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName()))) {
            Film updateFilm = (Film) films.values().stream().filter(f -> f.getName().equals(film.getName()));
            films.put(updateFilm.getId(), film);
            log.info("Фильм: '{}' - обновлен", film);
        } else {
            films.put(film.getId(), film);
            log.info("Фильм: '{}' - обновлен", film);
        }
        return film;
    }
}
