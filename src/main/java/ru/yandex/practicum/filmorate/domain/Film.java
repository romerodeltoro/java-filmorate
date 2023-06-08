package ru.yandex.practicum.filmorate.domain;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.MinPast;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Slf4j
@Data
@Builder
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @MinPast(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
}
