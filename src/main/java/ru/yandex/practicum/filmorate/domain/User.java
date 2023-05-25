package ru.yandex.practicum.filmorate.domain;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Slf4j
@Data
@Builder
public class User {
    private long id;
    @NotBlank (message = "Электронная почта не может быть пустой")
    @Email (message = "Электронная почта должна содержать символ @")
    private String email;
    @NotBlank (message = "Логин не может быть пустым")
    private String login;

    private String name;
    @Past (message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

}
