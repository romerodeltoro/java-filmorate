package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.MpaDaoImpl;

import java.util.List;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class MpaService {

    private final MpaDaoImpl mpaDao;

    public Mpa getMpa(Long id) {
        if (mpaDao.getMpa(id) == null) {
            throw new MpaNotFoundException(String.format("Рейтинга с id %d нет в базе", id));
        }
        return mpaDao.getMpa(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }
}
