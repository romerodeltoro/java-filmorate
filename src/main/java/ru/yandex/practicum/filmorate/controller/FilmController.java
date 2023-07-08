package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;


@Slf4j
@Getter
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    /**
     * Получаем все фильмы
     */
    @GetMapping
    public ResponseEntity<Collection<Film>> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return ResponseEntity.ok().body(filmService.getFilmStorage().getAllFilms());
    }

    /**
     * Добавляем фильм в базу
     */
    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        filmService.addFilm(film);
        log.info("Добавлен новый фильм: id - '{}', name - '{}'", film.getId(), film.getName());
        return ResponseEntity.ok().body(film);

    }

    /**
     * Обновляем фильм
     */
    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        filmService.updateFilm(film);
        log.info("Фильм: '{}' - обновлен", film);
        return ResponseEntity.ok().body(film);
    }


    /**
     * Получаем фильм
     */
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        log.info("Получены данные о фильме с id '{}'", id);
        return ResponseEntity.ok().body(filmService.getFilm(id));
    }

    /**
     * Юзер ставит фильму лайк
     */
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        filmService.addLikeToFilm(id, userId);
        log.info("Пользователь с id '{}' добавил лайк фильму с id '{}'", userId, id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Юзер удаляет лайк    
     */
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        filmService.removeLikeFromFilm(id, userId);
        log.info("Пользователь с id '{}' убрал лайк фильму с id '{}'", userId, id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Получаем самые популярные фильмы
     */
    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getMostLikeFilms(@RequestParam(required = false) Long count) {
        log.info("Получам спискок из '{}' самых популярных фильмов", count);
        return ResponseEntity.ok().body(filmService.getMostLikeFilms(count));
    }


}
