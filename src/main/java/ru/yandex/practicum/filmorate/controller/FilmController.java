package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Getter
@Validated
@RestController
@RequestMapping("films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return ResponseEntity.ok().body(filmService.getFilmStorage().getFilms());
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        filmService.getFilmStorage().addFilm(film);
        log.info("Добавлен новый фильм: id - '{}', name - '{}'", film.getId(), film.getName());
        return ResponseEntity.ok().body(film);

    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        filmService.getFilmStorage().updateFilm(film);
        log.info("Фильм: '{}' - обновлен", film);
        return ResponseEntity.ok().body(film);
    }
}
