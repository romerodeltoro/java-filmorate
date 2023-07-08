package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void addLikeToFilm(long filmId, long userId);

    void removeLike(Long filmId, Long userId);

    public Collection<Film> getMostLikeFilms();
}
