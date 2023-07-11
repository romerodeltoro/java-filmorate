package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDao;

import java.util.List;


@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpa(Long id) {
        String sqlQuery = "SELECT * " +
                "FROM mpa " +
                "WHERE mpa_id = ?";

        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, mpaRowMapper(), id);
        if (mpas.size() != 1) {
            throw new MpaNotFoundException(String.format("Рейтинга с id %d нет в базе", id));
        }
        return mpas.get(0);

    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM mpa", mpaRowMapper());
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> Mpa.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
