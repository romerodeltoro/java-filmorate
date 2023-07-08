package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    @Qualifier("filmStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("userStorage")
    private UserStorage userStorage;


    /**
     * Добавляем фильм в базу
     */
    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    /**
     * Получаем фильм
     */
    public Film getFilm(Long id) {
        if (filmStorage.getFilm(id) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", id));
        }
        return filmStorage.getFilm(id);
    }


    /**
     * Обновляем фильм
     */
    public void updateFilm(Film film) {
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", film.getId()));
        } else {
            filmStorage.updateFilm(film);
        }
    }


    /**
     * Пользователь добавляет лайк фильму
     */
    public void addLikeToFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", filmId));
        } else if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", userId));
        } else {
            filmStorage.addLikeToFilm(filmId, userId);
        }
    }

    /**
     * Убераем лайк от фильма
     */
    public void removeLikeFromFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", filmId));
        } else if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", userId));
        } else {
            filmStorage.removeLike(filmId, userId);
        }
    }

    /**
     * Получаем список из самых поулярных фильмов
     */
    public Collection<Film> getMostLikeFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        int countFilms = filmStorage.getMostLikeFilms().size();

        if (countFilms == 0) {
            return filmStorage.getAllFilms();
        }
        return filmStorage.getMostLikeFilms().stream()
                .sorted((f1, f2) -> (f2.getLikes() - f1.getLikes()))
                .limit(count)
                .collect(Collectors.toSet());


    }
}
