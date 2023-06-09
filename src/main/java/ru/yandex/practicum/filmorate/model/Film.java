package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.MinPast;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Data
@NoArgsConstructor
public class Film {


    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @MinPast(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
    private Integer likes;


    public Film(String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa, Integer likes) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = likes;
    }

    @JsonSetter
    public void setGenres(Set<Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
    }

}
