package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.MpaDaoImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaControllerTest {

    private final MpaDaoImpl mpaDao;

    @Test
    @DisplayName("Получение списка рейтингов")
    void findAll() {
        final List<Mpa> listMpa = mpaDao.getAllMpa();
        int size = listMpa.size();

        assertNotNull(listMpa, "Рейтинги не возвращаются.");
        assertEquals(5, size, "Количество рейтингов не совпадает.");
    }

    @Test
    @DisplayName("Получение существующего рейтинга")
    void getMpaWithRightId() {
        final Mpa mpa = mpaDao.getMpa(1);
        final int id = mpa.getId();

        assertEquals(mpa,
                mpaDao.getMpa(id), "Рейтинги не совпадают.");
    }

    @Test
    @DisplayName("Получение рейтинга по неверному id")
    void getMpaWithNotExistsId() {
        int id = 666;
        final MpaNotFoundException e = assertThrows(
                MpaNotFoundException.class,
                () -> mpaDao.getMpa(id)
        );
        assertEquals("Рейтинга с id " + id + " нет в базе", e.getMessage());
    }
}
