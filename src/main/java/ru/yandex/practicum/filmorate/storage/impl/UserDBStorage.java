package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("userStorage")
@RequiredArgsConstructor
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> params = new HashMap<>();
        params.put("email", user.getEmail());
        params.put("login", user.getLogin());
        params.put("birthday", user.getBirthday());
        if (user.getName() != null) {
            params.put("name", user.getName());
        } else {
            user.setName(user.getLogin());
            params.put("name", user.getLogin());
        }
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.longValue());

        return user;
    }

    @Override
    public User getUser(Long id) {

        List<User> users = jdbcTemplate.query(
                "SELECT * " +
                        "FROM users " +
                        "WHERE user_id = ?", userRowMapper(), id);
        if (users.size() != 1) {
            throw new UserNotFoundException(String.format("Пользователя с id %d нет в базе", id));
        }
        return users.get(0);
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(
                "SELECT * " +
                        "FROM users", userRowMapper());
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery =
                "UPDATE users " +
                        "SET email = ?, login = ?, name = ?, birthday = ? " +
                        "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public void addUserFriend(Long id, Long friendId) {
        String sqlQuery =
                "INSERT INTO friends(user_id, friend_id) " +
                        "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void removeUserFriend(Long id, Long friendId) {
        String sqlQuery =
                "DELETE FROM friends " +
                        "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public List<User> getUserFriends(Long id) {
        String sqlQuery =
                "SELECT * " +
                        "FROM users " +
                        "WHERE user_id IN " +
                        "(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, userRowMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sqlQuery =
                "SELECT * " +
                        "FROM users " +
                        "WHERE user_id IN " +
                        "(SELECT f1.friend_id " +
                        "FROM friends f1 " +
                        "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                        "WHERE f1.user_id = ? AND f2.user_id = ?)";
        return jdbcTemplate.query(sqlQuery, userRowMapper(), id, otherId);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(LocalDate.parse(rs.getString("birthday")))
                .build();
    }
}