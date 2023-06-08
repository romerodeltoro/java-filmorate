package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    /* Добавляет лайк фильму */
    public void addLikeToFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", filmId));
        } else if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", userId));
        } else {
            filmStorage.getFilm(filmId).getLikes().add(userId);
        }
    }

    /* Убераем лайк от фильму */
    public void removeLikeFromFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", filmId));
        } else if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", userId));
        } else {
            filmStorage.getFilm(filmId).getLikes().remove(userId);
        }
    }

    /* Получаем список из самых популярных фильмов */
    public List<Film> getMostLikeFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> (f2.getLikes().size() - f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
