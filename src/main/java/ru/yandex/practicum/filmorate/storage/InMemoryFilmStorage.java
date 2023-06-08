package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private HashMap<Long, Film> films = new HashMap<>();
    private static long incrementedFilmId = 0;

    private long setIncrementedFilmId() {
        return ++incrementedFilmId;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public void addFilm(Film film) {
        film.setId(setIncrementedFilmId());
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        if (films.values().stream().anyMatch(f -> f.getId() == film.getId())) {
            long id = films.values().stream()
                    .filter(f -> f.getId() == film.getId())
                    .findFirst()
                    .get()
                    .getId();
            films.put(id, film);
        } else {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", film.getId()));
        }
    }
}
