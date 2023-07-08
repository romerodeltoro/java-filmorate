package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("filmStorage")
@RequiredArgsConstructor
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Collection<Film> getAllFilms() {

        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "f.mpa_id, m.mpa_name, g.genre_id, g.genre_name " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                        "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id ";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Map<Long, Film> films = new HashMap<Long, Film>();
            while (rs.next()) {

                long filmId = rs.getLong("film_id");
                Film film = films.get(filmId);
                if (film == null) {
                    film = new Film();
                    film.setId(rs.getLong("film_id"));
                    film.setName(rs.getString("name"));
                    film.setDescription((rs.getString("description")));
                    film.setReleaseDate((LocalDate.parse(rs.getString("releaseDate"))));
                    film.setDuration((rs.getInt("duration")));
                    film.setMpa(Mpa.builder()
                            .id(rs.getInt("mpa_id"))
                            .name(rs.getString("mpa_name"))
                            .build());
                    if (rs.getInt("genre_id") != 0) {
                        Set<Genre> genres = new HashSet<>();
                        genres.add(Genre.builder()
                                .id(rs.getInt("genre_id"))
                                .name(rs.getString("genre_name"))
                                .build());
                        film.setGenres(genres);
                    }
                    films.put(filmId, film);
                }
                Genre.GenreBuilder genreBuilder = Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("genre_name"));
                if (film.getGenres().size() != 0 && !film.getGenres().contains(genreBuilder.build())) {
                    film.getGenres().add(genreBuilder.build());
                }
            }
            return films.values();
        });
    }


    @Override
    public Film getFilm(Long id) {

        String sqlQuery2 =
                "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "f.mpa_id, m.mpa_name, g.genre_id, g.genre_name, count(fl.user_id) " +
                        "FROM films AS f " +
                        "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                        "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                        "LEFT JOIN film_likes AS fl ON f.FILM_ID = fl.FILM_ID " +
                        "WHERE f.film_id = ? " +
                        "GROUP BY f.film_id, G.GENRE_ID";

        List<Film> films = jdbcTemplate.query(sqlQuery2, filmRowMapper(), id);
        if (films.size() < 1) {
            throw new FilmNotFoundException(String.format("Фильма с id %d нет в базе", id));
        }
        return films.get(0);
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery =
                "INSERT INTO films(name, description, releaseDate, duration, mpa_id)" +
                        "VALUES(?, ?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setObject(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        insertGenres(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        String sqlQuery =
                "UPDATE films " +
                        "SET name = ?, description = ?, releaseDate = ?, duration = ?,  mpa_id = ? " +
                        "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        insertGenres(film);

        return film;
    }

    @Override
    public void addLikeToFilm(long filmId, long userId) {
        String sqlQuery =
                "INSERT INTO film_likes (film_id, user_id) " +
                        "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sqlQuery =
                "DELETE FROM film_likes " +
                        "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    public Collection<Film> getMostLikeFilms() {
        String sqlQuery =
                "SELECT COUNT(fl.user_id), f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                        "m.mpa_id, m.mpa_name, g.genre_id, g.genre_name " +
                        "FROM film_likes fl " +
                        "JOIN films f ON fl.film_id = f.film_id " +
                        "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                        "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                        "GROUP BY f.film_id, m.mpa_id, g.genre_id " +
                        "ORDER BY COUNT(fl.user_id) DESC";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Map<Long, Film> films = new HashMap<Long, Film>();
            while (rs.next()) {

                long filmId = rs.getLong("film_id");
                Film film = films.get(filmId);
                if (film == null) {
                    film = new Film();
                    film.setId(rs.getLong("film_id"));
                    film.setName(rs.getString("name"));
                    film.setDescription((rs.getString("description")));
                    film.setReleaseDate((LocalDate.parse(rs.getString("releaseDate"))));
                    film.setDuration((rs.getInt("duration")));
                    film.setLikes(rs.getInt("COUNT(fl.user_id)"));
                    film.setMpa(Mpa.builder()
                            .id(rs.getInt("mpa_id"))
                            .name(rs.getString("mpa_name"))
                            .build());
                    if (rs.getInt("genre_id") != 0) {
                        Set<Genre> genres = new HashSet<>();
                        genres.add(Genre.builder()
                                .id(rs.getInt("genre_id"))
                                .name(rs.getString("genre_name"))
                                .build());
                        film.setGenres(genres);
                    }
                    films.put(filmId, film);
                }
                Genre.GenreBuilder genreBuilder = Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("genre_name"));
                if (film.getGenres().size() != 0 && !film.getGenres().contains(genreBuilder.build())) {
                    film.getGenres().add(genreBuilder.build());
                }
            }
            return films.values();
        });


    }

    private RowMapper<Film> filmRowMapper() {

        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription((rs.getString("description")));
            film.setReleaseDate((LocalDate.parse(rs.getString("releaseDate"))));
            film.setDuration((rs.getInt("duration")));
            film.setMpa(Mpa.builder()
                    .id(rs.getInt("mpa_id"))
                    .name(rs.getString("mpa_name"))
                    .build());
            film.setLikes(rs.getInt("count(fl.user_id)"));
            Set<Genre> genres = new HashSet<>();
            if (rs.getInt("genre_id") != 0) {
                do {
                    Genre.GenreBuilder genreBuilder = Genre.builder()
                            .id(rs.getInt("genre_id"))
                            .name(rs.getString("genre_name"));
                    genres.add(genreBuilder.build());
                } while (rs.next());
            }
            film.setGenres(genres);

            return film;

        };
    }

    private void insertGenres(Film film) {
        Set<Genre> genres = film.getGenres();

        String deleteQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteQuery, film.getId());

        if (genres != null && !genres.isEmpty()) {
            String query = "INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)";
            genres.forEach(genre -> jdbcTemplate.update(query, film.getId(), genre.getId()));
        }
    }
}