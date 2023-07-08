package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenre(int id) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT * " +
                        "FROM genres " +
                        "WHERE genre_id = ?", genreRowMapper(), id);
        if (genres.size() != 1) {
            throw new GenreNotFoundException(String.format("Жанра с id %d нет в базе", id));
        }
        return genres.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM genres", genreRowMapper());
    }

    public List<Genre> getGenresForFilm(Long id) {
        return jdbcTemplate.query(
                "select fg.genre_id, g.genre_name\n" +
                        "from genres g \n" +
                        "join film_genre fg on g.genre_id = fg.genre_id \n" +
                        "where film_id = ?", genreRowMapper(), id);
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();

    }
}
