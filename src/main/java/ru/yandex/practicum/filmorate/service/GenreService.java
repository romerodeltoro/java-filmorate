package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class GenreService {

    private final GenreDao genreDao;

    public Genre getGenre(int id) {
        return genreDao.getGenre(id);
    }

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
