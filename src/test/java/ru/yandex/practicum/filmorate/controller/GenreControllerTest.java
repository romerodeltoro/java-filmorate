package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.impl.GenreDaoImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreControllerTest {

    private final GenreDaoImpl genreDao;

    @Test
    @DisplayName("Получение списка жанров")
    void findAll() {
        final List<Genre> genres = genreDao.getAllGenres();
        int size = genres.size();

        assertNotNull(genres, "Жанры не возвращаются.");
        assertEquals(6, size, "Количество жанров не совпадает.");
    }

    @Test
    @DisplayName("Получение существующего жанра")
    void getMpaWithRightId() {
        final Genre genre = genreDao.getGenre(1L);
        final Long id = genre.getId();

        assertEquals(genre,
                genreDao.getGenre(id), "Рейтинги не совпадают.");
    }

    @Test
    @DisplayName("Получение жанра по неверному id")
    void getMpaWithNotExistsId() {
        Long id = 666L;
        final GenreNotFoundException e = assertThrows(
                GenreNotFoundException.class,
                () -> genreDao.getGenre(id)
        );
        assertEquals("Жанра с id " + id + " нет в базе", e.getMessage());
    }

}