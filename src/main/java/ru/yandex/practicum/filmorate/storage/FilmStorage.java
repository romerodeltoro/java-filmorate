package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.domain.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();
    void addFilm(Film film);
    void updateFilm(Film film);
}
