package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private HashMap<Long, Film> filmsMap = new HashMap<>();
    private static long incrementedFilmId = 0;

    private long setIncrementedFilmId() {
        return ++incrementedFilmId;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>();
    }

    @Override
    public Film getFilm(Long id) {
        if (filmsMap.containsKey(id)) {
            return filmsMap.get(id);
        } else {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", id));
        }
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(setIncrementedFilmId());
        filmsMap.put(film.getId(), film);
        return film;

    }

    @Override
    public Film updateFilm(Film film) {
        if (filmsMap.values().stream().anyMatch(f -> f.getId() == film.getId())) {
            long id = filmsMap.values().stream()
                    .filter(f -> f.getId() == film.getId())
                    .findFirst()
                    .get()
                    .getId();
            filmsMap.put(id, film);
        } else {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", film.getId()));
        }
        return film;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {

    }

    @Override
    public void removeLike(Long filmId, Long userId) {

    }

    @Override
    public Collection<Film> getMostLikeFilms() {
        return null;
    }
}
