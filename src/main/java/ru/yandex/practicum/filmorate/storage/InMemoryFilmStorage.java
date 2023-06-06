package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.domain.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    private HashMap<Long, Film> films = new HashMap<>();
    private static long incrementedFilmId = 0;

    private long setIncrementedFilmId() {
        return ++incrementedFilmId;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addFilm(Film film) {
        film.setId(setIncrementedFilmId());
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName()))) {
            long id = films.values().stream()
                    .filter(f -> f.getName().equals(film.getName()))
                    .findFirst()
                    .get()
                    .getId();
            films.put(id, film);
        } else {
            film.setId(setIncrementedFilmId());
            films.put(film.getId(), film);
        }
    }
}
