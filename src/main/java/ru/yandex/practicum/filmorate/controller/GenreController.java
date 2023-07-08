package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    /**
     * Выводим список рейтингов
     */
    @GetMapping
    public ResponseEntity<Collection<Genre>> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return ResponseEntity.ok().body(genreService.getAllGenres());
    }


    /**
     * Получаем рейтинг по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable int id) {
        log.info("Получен рейтинг с id '{}'", id);

        return ResponseEntity.ok().body(genreService.getGenre(id));
    }
}
