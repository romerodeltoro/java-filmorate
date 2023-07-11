package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    Mpa getMpa(Long id);

    List<Mpa> getAllMpa();
}
